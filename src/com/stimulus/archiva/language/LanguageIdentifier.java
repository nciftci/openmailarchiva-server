
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Identify the language of a content, based on statistical analysis.
 *
 * @see <a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639
 *      Language Codes</a>
 * 
 * @author Sami Siren
 * @author J&eacute;r&ocirc;me Charron
 * @author Jamie Band
 */

package com.stimulus.archiva.language;

// JDK imports
import java.io.*;
import java.util.*;
import com.stimulus.archiva.language.NGramProfile.NGramEntry;
import org.apache.commons.logging.*;


public class LanguageIdentifier implements Serializable {
  
  /**
	 * 
	 */
	private static final long serialVersionUID = 6583266226548053224L;

private final static int MINIMUM_SAMPLE_LENGTH = 300;
	 
  private final static int DEFAULT_ANALYSIS_LENGTH = 1500;    // 0 means full content
  
  protected static final Log logger = LogFactory.getLog(LanguageIdentifier.class.getName());
    
  private ArrayList languages = new ArrayList();

  private ArrayList supportedLanguages = new ArrayList();

  /** Minimum size of NGrams */
  private int minLength = NGramProfile.DEFAULT_MIN_NGRAM_LENGTH;
  
  /** Maximum size of NGrams */
  private int maxLength = NGramProfile.DEFAULT_MAX_NGRAM_LENGTH;
  
  /** The maximum amount of data to analyze */
  public int analyzeLength = DEFAULT_ANALYSIS_LENGTH;
  
  /** A global index of ngrams of all supported languages */
  private HashMap ngramsIdx = new HashMap();

  /** The NGramProfile used for identification */
  private NGramProfile suspect = null;

  /** My singleton instance */
  private static LanguageIdentifier identifier = null;


  /**
   * Constructs a new Language Identifier.
   */
  public LanguageIdentifier() {

    // Gets ngram sizes to take into account from the Nutch Config
    minLength = NGramProfile.DEFAULT_MIN_NGRAM_LENGTH;
    maxLength = NGramProfile.DEFAULT_MAX_NGRAM_LENGTH;
    // Ensure the min and max values are in an acceptale range
    // (ie min >= DEFAULT_MIN_NGRAM_LENGTH and max <= DEFAULT_MAX_NGRAM_LENGTH)
    maxLength = Math.min(maxLength, NGramProfile.ABSOLUTE_MAX_NGRAM_LENGTH);
    maxLength = Math.max(maxLength, NGramProfile.ABSOLUTE_MIN_NGRAM_LENGTH);
    minLength = Math.max(minLength, NGramProfile.ABSOLUTE_MIN_NGRAM_LENGTH);
    minLength = Math.min(minLength, maxLength);

    // Gets the value of the maximum size of data to analyze
    analyzeLength = DEFAULT_ANALYSIS_LENGTH;
    
    Properties p = new Properties();
    try {
        
      p.load(this.getClass().getResourceAsStream("langmappings.properties"));

      Enumeration alllanguages = p.keys();
      
      logger.debug("language identifier configuration {minLength='"+minLength+"',maxLength='"+maxLength+"',analyzeLength='"+analyzeLength+"'}");
     
      StringBuffer list = new StringBuffer("language identifier service supports:");
      HashMap tmpIdx = new HashMap();
      while (alllanguages.hasMoreElements()) {
        String lang = (String) (alllanguages.nextElement());
       
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/stimulus/archiva/language/" + lang + "." + NGramProfile.FILE_EXTENSION);

        if (is != null) {
          NGramProfile profile = new NGramProfile(lang, minLength, maxLength);
          try {
            profile.load(is);
            languages.add(profile);
            supportedLanguages.add(lang);
            List ngrams = profile.getSorted();
            for (int i=0; i<ngrams.size(); i++) {
                NGramEntry entry = (NGramEntry) ngrams.get(i);
                List registered = (List) tmpIdx.get(entry);
                if (registered == null) {
                    registered = new ArrayList();
                    tmpIdx.put(entry, registered);
                }
                registered.add(entry);
                entry.setProfile(profile);
            }
            list.append(" " + lang + "(" + ngrams.size() + ")");
            is.close();
          } catch (IOException e1) {
              logger.error("failed to initialize language identifier module",e1);
          }
        }
      }
      // transform all ngrams lists to arrays for performances
      Iterator keys = tmpIdx.keySet().iterator();
      while (keys.hasNext()) {
        NGramEntry entry = (NGramEntry) keys.next();
        List l = (List) tmpIdx.get(entry);
        if (l != null) {
          NGramEntry[] array = (NGramEntry[]) l.toArray(new NGramEntry[l.size()]);
          ngramsIdx.put(entry.getSeq(), array);
        }
      }
      logger.debug(list.toString());
    
      // Create the suspect profile
      suspect = new NGramProfile("suspect", minLength, maxLength);
    } catch (Exception e) {
      logger.error("failed to initialize language identifier service",e); 
    }
  }


  
  /**
   * Identify language of a content.
   * 
   * @param content is the content to analyze.
   * @return The 2 letter
   *         <a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639
   *         language code</a> (en, fi, sv, ...) of the language that best
   *         matches the specified content.
   */
  public String identify(String content) {
    return identify(new StringBuffer(content));
  }

  /**
   * Identify language of content.
   * 
   * @param content is the content to analyze.
   * @return The 2 letter
   *         <a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639
   *         language code</a> (en, fi, sv, ...) of the language that best
   *         matches the specified content.
   */
  public synchronized String identify(StringBuffer content) {
    //logger.debug("language identification sample:");
    //logger.debug(content.toString());
    StringBuffer text = content;
    if ((analyzeLength > 0) && (content.length() > analyzeLength)) {
        text = new StringBuffer().append(content);
        text.setLength(analyzeLength);
    }

    suspect.analyze(text);
    Iterator iter = suspect.getSorted().iterator();
    float topscore = Float.MIN_VALUE;
    String lang = null;
    HashMap scores = new HashMap();
    NGramEntry searched = null;
    
    while (iter.hasNext()) {
        searched = (NGramEntry) iter.next();
        NGramEntry[] ngrams = (NGramEntry[]) ngramsIdx.get(searched.getSeq());
        if (ngrams != null) {
            for (int j=0; j<ngrams.length; j++) {
                NGramProfile profile = ngrams[j].getProfile();
                Float pScore = (Float) scores.get(profile);
                if (pScore == null) {
                    pScore = new Float(0);
                }
                float plScore = pScore.floatValue();
                plScore += ngrams[j].getFrequency() + searched.getFrequency();
                scores.put(profile, new Float(plScore));
                if (plScore > topscore) {
                    topscore = plScore;
                    lang = profile.getName();
                }
            }
        }
    }
    logger.debug("document language identified {language='"+lang+"'}");
    return lang;
  }
 
  public String identify(Reader reader) throws IOException {

    StringBuffer out = new StringBuffer();
    char[] buffer = new char[2048];
    int len = 0;

    while (((len = reader.read(buffer)) != -1) &&
           ((analyzeLength == 0) || (out.length() < analyzeLength))) {
      if (analyzeLength != 0) {
          len = Math.min(len, analyzeLength - out.length());
      }
      out.append(buffer, 0, len);
    }
   
    if (out.length()<MINIMUM_SAMPLE_LENGTH) {
    	logger.debug("the sample is too small to reliably detect the language.");
    	return null;
    }
    return identify(out);
  }
  
  public int getAnalyzeLength() {
      return analyzeLength;
  }
  
  

}
