import xml.etree.ElementTree as ET
import json
import sys
import time

"""
Questo script converte un file XML in un formato JSON specifico (bizjson) utilizzato da PagoPA.
Il file XML deve essere strutturato secondo lo schema definito in pagopa-api.pagopa.gov.it/pa/paForNode.xsd.
Esempio di utilizzo:
python xml_to_bizjson.py paSendRTReq_input.xml biz_output.json
"""

def map_xml_to_bizjson(root):
    # Esempio di mapping: adatta questa funzione alla struttura reale del tuo XML
    return {
        "id": root.findtext("idPA", default="") + "_" + root.findtext("receipt/receiptId", default=""),
        "version": "2",
        "complete": "true",
        "missingInfo": ["transactionDetails"],
        "debtorPosition": {
            "modelType": "2",
            "noticeNumber": root.findtext("receipt/noticeNumber", default=""),
            "iuv": root.findtext("receipt/creditorReferenceId", default="")
        },
        "creditor": {
            "idPA": root.findtext("idPA", default=""),
            "idBrokerPA": root.findtext("idBrokerPA", default=""),
            "idStation": root.findtext("idStation", default=""),
            "companyName": root.findtext("receipt/companyName", default=""),
            "officeName": root.findtext("receipt/officeName", default="")
        },
        "psp": {
            "idPsp": root.findtext("receipt/idPSP", default=""),
            "idBrokerPsp": root.findtext("receipt/pspFiscalCode", default=""),
            "idChannel": root.findtext("receipt/idChannel", default=""),
            "psp": root.findtext("receipt/PSPCompanyName", default=""),
        },
        "debtor": {
            "fullName": root.findtext("receipt/debtor/fullName", default=""),
            "entityUniqueIdentifierType": root.findtext("receipt/debtor/uniqueIdentifier/entityUniqueIdentifierType", default=""),
            "entityUniqueIdentifierValue": root.findtext("receipt/debtor/uniqueIdentifier/entityUniqueIdentifierValue", default=""),
        },
        # "payer": {
        #     "fullName": root.findtext("receipt/debtor/fullName", default=""),
        #     "entityUniqueIdentifierType": root.findtext("receipt/debtor/uniqueIdentifier/entityUniqueIdentifierType", default=""),
        #     "entityUniqueIdentifierValue": root.findtext("receipt/debtor/uniqueIdentifier/entityUniqueIdentifierValue", default=""),
        # },
        "paymentInfo": {
            "paymentDateTime": root.findtext("receipt/paymentDateTime", default=""),
            "paymentToken": "223F665500001336354",
            "amount": root.findtext("receipt/paymentAmount", default=""),
            "fee": root.findtext("receipt/fee", default=""),
            "paymentMethod": root.findtext("receipt/paymentMethod_NOT_USED", default="CP"), ## forced CP
            "remittanceInformation": root.findtext("receipt/transferList/transfer/remittanceInformation", default=""),
        },
        "transferList": [
            {
                "idTransfer": root.findtext("receipt/transferList/transfer/idTransfer", default=""),
                "fiscalCodePA": root.findtext("receipt/transferList/transfer/fiscalCodePA", default=""),
                "amount": root.findtext("receipt/transferList/transfer/transferAmount", default=""),
                "transferCategory": root.findtext("receipt/transferList/transfer/transferCategory", default=""),
                "remittanceInformation": root.findtext("receipt/transferList/transfer/remittanceInformation", default=""),
                "IBAN": root.findtext("receipt/transferList/transfer/IBAN", default=""),
            }
        ],
        "transactionDetails": {
            "transaction": {
                "origin": root.findtext("receipt/channelDescription_NOT_USED", default="IO"), ## forced IO
                "amount": int(round(float(root.findtext("receipt/paymentAmount", default="").replace(",", ".")) * 100)), ## euro_cent
                "fee": int(round(float(root.findtext("receipt/fee", default="").replace(",", ".")) * 100)), ## euro_cent
                "grandTotal": int(round(float(root.findtext("receipt/paymentAmount", default="").replace(",", ".")) * 100)) +  int(round(float(root.findtext("receipt/fee", default="").replace(",", ".")) * 100)), ## euro_cent                
            },
            "user": {
                "fiscalCode": root.findtext("receipt/debtor/uniqueIdentifier/entityUniqueIdentifierValue", default=""),
            },
            "info": {
                "clientId": root.findtext("receipt/channelDescription_NOT_USED", default="IO"), ## forced IO
                "paymentMethodName": root.findtext("receipt/paymentMethod_NOT_USED", default="CARDS"), ## forced CARDS
                "type": root.findtext("receipt/paymentMethod_NOT_USED", default="CP"), ## forced CP
            }               
        }, 
        "timestamp": int(time.time() * 1000),
        "properties": {
            "serviceIdentifier": "NDP002PROD_RR"
        }
        # Continua il mapping per tutti i campi necessari...
    }

def main(xml_file, json_file):
    namespaces = {
        'soapenv': 'http://schemas.xmlsoap.org/soap/envelope/',
        'pafn': 'http://pagopa-api.pagopa.gov.it/pa/paForNode.xsd'
    }
    tree = ET.parse(xml_file)
    root = tree.getroot()

    # Naviga fino al nodo che ti interessa
    paSendRTReq = root.find('.//pafn:paSendRTReq', namespaces)

    bizjson = map_xml_to_bizjson(paSendRTReq)
    with open(json_file, "w", encoding="utf-8") as f:
        json.dump(bizjson, f, indent=2, ensure_ascii=False)
    print(f"Creato {json_file} a partire da {xml_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Uso: python xml_to_bizjson.py input.xml output.json")
        sys.exit(1)
    main(sys.argv[1], sys.argv[2])