package it.gov.pagopa.bizevents.sync.nodo.entity.nodo.newmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "POSITION_SUBJECT")
public class PositionSubject {

  @Id
  @Column(name = "ID")
  private Long id;

  @Column(name = "SUBJECT_TYPE")
  private String subjectType;

  @Column(name = "ENTITY_UNIQUE_IDENTIFIER_TYPE")
  private String entityUniqueIdentifierType;

  @Column(name = "ENTITY_UNIQUE_IDENTIFIER_VALUE")
  private String entityUniqueIdentifierValue;

  @Column(name = "FULL_NAME")
  private String fullName;

  @Column(name = "STREET_NAME")
  private String streetName;

  @Column(name = "CIVIC_NUMBER")
  private String civicNumber;

  @Column(name = "POSTAL_CODE")
  private String postalCode;

  @Column(name = "CITY")
  private String city;

  @Column(name = "STATE_PROVINCE_REGION")
  private String stateProvinceRegion;

  @Column(name = "COUNTRY")
  private String country;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "INSERTED_TIMESTAMP")
  private LocalDateTime insertedTimestamp;

  @Column(name = "UPDATED_TIMESTAMP")
  private LocalDateTime updatedTimestamp;

  @Column(name = "INSERTED_BY")
  private String insertedBy;

  @Column(name = "UPDATED_BY")
  private String updatedBy;
}
