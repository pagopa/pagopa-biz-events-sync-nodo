package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The outcome of the synchronization of a single receipt.")
public enum SyncSingleRecordStatus {

    @Schema(description = "The BizEvents was re-generated with success.")
    GENERATED,

    @Schema(description = "The BizEvents was not re-generated due to an error.")
    NOT_INSERTED
}
