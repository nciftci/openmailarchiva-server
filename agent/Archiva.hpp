/*
 * This file was auto-generated by the Axis C++ Web Service Generator (WSDL2Ws)
 * This file contains Client Stub Class for remote web service 
 */

#if !defined(__ARCHIVA_CLIENTSTUB_H__INCLUDED_)
#define __ARCHIVA_CLIENTSTUB_H__INCLUDED_

#include <axis/client/Stub.hpp>
#include <axis/SoapFaultException.hpp>
#include <axis/ISoapFault.hpp>
AXIS_CPP_NAMESPACE_USE

class Archiva :public Stub
{
public:
	STORAGE_CLASS_INFO Archiva(const char* pchEndpointUri, AXIS_PROTOCOL_TYPE eProtocol=APTHTTP1_1);
	STORAGE_CLASS_INFO Archiva();
public:
	STORAGE_CLASS_INFO virtual ~Archiva();
public: 
	STORAGE_CLASS_INFO xsd__string getServerVersion();
	STORAGE_CLASS_INFO void storeMessage(xsd__base64Binary Value0);
};

#endif /* !defined(__ARCHIVA_CLIENTSTUB_H__INCLUDED_)*/