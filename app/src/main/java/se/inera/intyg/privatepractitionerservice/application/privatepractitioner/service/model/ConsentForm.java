package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.model;

/**
 * Model representing a consent form (medgivande) with its version and content (html markup).
 */
public record ConsentForm(Long version, String form) {

}
