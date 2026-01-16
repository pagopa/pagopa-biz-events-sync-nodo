locals {
  apim_bizevents_sync_nodo_api = {
    display_name          = "Biz Events - Recovery & Sync"
    description           = "API to handle manual synchronization between NdP receipts and BizEvents"
    path                  = "bizevents/nodo-sync"
    subscription_required = true
    service_url           = null
  }
}


#####################
##  API Manual Ops ##
#####################

resource "azurerm_api_management_api_version_set" "api_bizevents_sync_nodo_manual_operation" {
  name                = "${var.env_short}-bizevents-sync-nodo-manual-operation"
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = local.apim_bizevents_sync_nodo_api.display_name
  versioning_scheme   = "Segment"
}


module "apim_bizevents_sync_nodo_manual_operation_v1" {
  source = "./.terraform/modules/__v3__/api_management_api"

  name                  = "${local.project}-bizevents-sync-nodo-manual-operation"
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id]
  subscription_required = local.apim_bizevents_sync_nodo_api.subscription_required
  version_set_id        = azurerm_api_management_api_version_set.api_bizevents_sync_nodo_manual_operation.id
  api_version           = "v1"

  description  = local.apim_bizevents_sync_nodo_api.description
  display_name = local.apim_bizevents_sync_nodo_api.display_name
  path         = local.apim_bizevents_sync_nodo_api.path
  protocols    = ["https"]
  service_url  = local.apim_bizevents_sync_nodo_api.service_url

  content_format = "openapi"

  content_value = templatefile("../openapi/openapi.json", {
    host = local.apim_hostname
  })

  xml_content = templatefile("./policy/v1/_base_policy.xml.tpl", {
    hostname = local.hostname
  })
}