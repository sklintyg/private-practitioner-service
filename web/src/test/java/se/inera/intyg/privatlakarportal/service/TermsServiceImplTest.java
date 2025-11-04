/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.privatlakarportal.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.privatlakarportal.common.exception.PrivatlakarportalServiceException;
import se.inera.intyg.privatlakarportal.integration.terms.services.dto.Terms;
import se.inera.intyg.privatlakarportal.persistence.model.MedgivandeText;
import se.inera.intyg.privatlakarportal.persistence.repository.MedgivandeTextRepository;

@RunWith(MockitoJUnitRunner.class)
public class TermsServiceImplTest {

  @Mock
  private MedgivandeTextRepository medgivandeTextRepository;

  @Mock
  private SubscriptionService subscriptionService;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private TermsServiceImpl termsService;

  @Test
  public void testGetTerms() {

    MedgivandeText medgivandeText = new MedgivandeText();
    medgivandeText.setVersion(1L);
    medgivandeText.setMedgivandeText("Testtext");
    medgivandeText.setDatum(LocalDate.parse("2015-09-01").atStartOfDay());
    when(medgivandeTextRepository.findLatest()).thenReturn(medgivandeText);

    Terms response = termsService.getTerms();
    assertEquals(LocalDate.parse("2015-09-01").atStartOfDay(), response.getDate());
    assertEquals("Testtext", response.getText());
    assertEquals(1L, response.getVersion());
  }

  @Test(expected = PrivatlakarportalServiceException.class)
  public void testGetTermsFail() {

    when(medgivandeTextRepository.findLatest()).thenReturn(null);

    termsService.getTerms();
  }

  @Test
  public void shouldReturnFetchedValueIfResponseOk() {
    final var hsaId = "HSA_ID";

    when(subscriptionService.isSubscriptionRequired()).thenReturn(false);

    setMockToReturnValue(HttpStatus.OK, true);
    final var trueResponse = termsService.getWebcertUserTermsApproved(hsaId);

    setMockToReturnValue(HttpStatus.OK, false);
    final var falseResponse = termsService.getWebcertUserTermsApproved(hsaId);

    assertTrue(trueResponse);
    assertFalse(falseResponse);
  }

  @Test
  public void shouldReturnFalseIfSubscriptionIsRequired() {
    final var hsaId = "HSA_ID";

    when(subscriptionService.isSubscriptionRequired()).thenReturn(true);
    final var falseResponse = termsService.getWebcertUserTermsApproved(hsaId);

    assertFalse(falseResponse);
  }

  @Test
  public void shouldReturnFalseIfFailedRestCall() {
    final var hsaId = "HSA_ID";

    when(subscriptionService.isSubscriptionRequired()).thenReturn(false);

    setMockToReturnValue(HttpStatus.INTERNAL_SERVER_ERROR, true);

    final var response = termsService.getWebcertUserTermsApproved(hsaId);

    assertFalse(response);
  }

  @Test
  public void shouldReturnFalseIfExceptionOnRestCall() {
    final var hsaId = "HSA_ID";

    when(subscriptionService.isSubscriptionRequired()).thenReturn(false);

    setMockToReturnException();

    final var response = termsService.getWebcertUserTermsApproved(hsaId);

    assertFalse(response);
  }

  private void setMockToReturnValue(HttpStatus httpStatus, Boolean responseBody) {
    doReturn(new ResponseEntity<>(responseBody, httpStatus)).when(restTemplate)
        .getForEntity(any(String.class), eq(Boolean.class));
  }

  private void setMockToReturnException() {
    when(restTemplate.getForEntity(any(String.class), eq(Boolean.class))).thenThrow(
        new RestClientException("Test RestClientException"));
  }
}
