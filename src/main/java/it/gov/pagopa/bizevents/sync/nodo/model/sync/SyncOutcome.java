package it.gov.pagopa.bizevents.sync.nodo.model.sync;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The outcome of the synchronization process.")
public enum SyncOutcome {

    @Schema(description = "The requested receipt was not found in the NdP databases.")
    RECEIPT_NOT_FOUND,

    @Schema(description = "An unexpected error occurred and the entire operation failed.")
    GENERATION_ERROR,

    @Schema(description = "An unexpected error occurred on a single BizEvent, but the operation continued. You can read which BizEvent was affected in the report records.")
    PARTIALLY_GENERATED,

    @Schema(description = "The synchronization operation was successful and the requested BizEvents were generated.")
    GENERATED,
}
