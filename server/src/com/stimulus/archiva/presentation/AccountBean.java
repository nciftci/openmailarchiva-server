/* Copyright (C) 2005 Jamie Angus Band 
 * This software program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.stimulus.archiva.presentation;


import com.stimulus.struts.ActionContext;
import com.stimulus.struts.BaseBean;
import org.apache.log4j.Logger;


public class AccountBean extends BaseBean {

  /* Constants */

    protected static final Logger logger = Logger.getLogger(MessageBean.class.getName());
    protected static final Logger audit = Logger.getLogger("com.stimulus.archiva.audit");

  public String signoff() {
    ActionContext.getActionContext().getRequest().getSession().invalidate();
    clear();
    return "success";
  }

}
