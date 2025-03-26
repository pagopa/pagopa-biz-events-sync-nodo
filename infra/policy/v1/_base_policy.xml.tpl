<policies>
  <backend>
    <base/>
  </backend>
  <inbound>
    <base/>
    <set-backend-service base-url="https://${hostname}/pagopa-biz-events-sync-nodo"/>
  </inbound>
  <on-error>
    <base/>
  </on-error>
  <outbound>
    <base/>
  </outbound>
</policies>
