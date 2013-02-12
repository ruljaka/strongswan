/*
 * Copyright (C) 2010-2013 Andreas Steffen
 * HSR Hochschule fuer Technik Rapperswil
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.  See <http://www.fsf.org/copyleft/gpl.txt>.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 */

/**
 * @defgroup tnccs tnccs
 * @ingroup tnc
 *
 * @defgroup tnccst tnccs
 * @{ @ingroup tnccs
 */

#ifndef TNCCS_H_
#define TNCCS_H_

typedef struct tnccs_t tnccs_t;
typedef enum tnccs_type_t tnccs_type_t;
typedef enum tnc_ift_type_t tnc_ift_type_t;

#include <tncif.h>
#include <tncifimc.h>
#include <tncifimv.h>

#include <library.h>
#include <plugins/plugin.h>

#include <tls.h>

/**
 * Type of TNC Client/Server protocol
 */
enum tnccs_type_t {
	TNCCS_UNKNOWN,
	TNCCS_1_1,
	TNCCS_SOH,
	TNCCS_2_0,
	TNCCS_DYNAMIC
};

/**
 * Type of TNC Transport protocol
 */
enum tnc_ift_type_t {
	TNC_IFT_UNKNOWN,
	TNC_IFT_EAP_1_0,
	TNC_IFT_EAP_1_1,
	TNC_IFT_EAP_2_0,
	TNC_IFT_TLS_1_0,
	TNC_IFT_TLS_2_0
};

/**
 * enum names for tnccs_type_t.
 */
extern enum_name_t *tnccs_type_names;

/**
 * TNCCS public interface
 */
struct tnccs_t {

	/**
	 * Implements tls_t
	 */
	tls_t tls;

	/**
	 * Get underlying TNC IF-T transport protocol
	 */
	tnc_ift_type_t (*get_transport)(tnccs_t *this);

	/**
	 * Set underlying TNC IF-T transport protocol

	 */
	void (*set_transport)(tnccs_t *this, tnc_ift_type_t transport);

	/**
	 * Get type of TNC Client authentication
	 */
	u_int32_t (*get_auth_type)(tnccs_t *this);

	/**
	 * Set type of TNC Client authentication
	 */
	void (*set_auth_type)(tnccs_t *this, u_int32_t auth_type);

};

/**
 * Constructor definition for a pluggable TNCCS protocol implementation.
 *
 * @param is_server		TRUE if TNC Server, FALSE if TNC Client
 * @param server		Server identity
 * @param peer			Client identity
 * @param transport		Underlying TNC IF-T transport protocol used
 * @return				implementation of the tnccs_t interface
 */
typedef tnccs_t *(*tnccs_constructor_t)(bool is_server,
										identification_t *server,
										identification_t *peer,
										tnc_ift_type_t transport);

/**
 * Callback function adding a message to a TNCCS batch
 *
 * @param imc_id		ID of IMC or TNC_IMCID_ANY
 * @param imc_id		ID of IMV or TNC_IMVID_ANY
 * @param msg_flags		message flags
 * @param msg			message to be added
 * @param msg_len		message length
 * @param msg_vid		message vendor ID
 * @param msg_subtype	message subtype
 * @return				return code
 */
typedef TNC_Result (*tnccs_send_message_t)(tnccs_t* tncss,
										  TNC_IMCID imc_id,
										  TNC_IMVID imv_id,
										  TNC_UInt32 msg_flags,
										  TNC_BufferReference msg,
										  TNC_UInt32 msg_len,
									 	  TNC_VendorID msg_vid,
										  TNC_MessageSubtype msg_subtype);

#endif /** TNCCS_H_ @}*/
