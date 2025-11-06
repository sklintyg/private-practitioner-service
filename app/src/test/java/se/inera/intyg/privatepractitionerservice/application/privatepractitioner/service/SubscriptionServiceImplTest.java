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
package se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

  @InjectMocks
  private SubscriptionServiceImpl subscriptionService;

  @Test
  void subscriptionInUseShouldReturnTrueIfSubscriptionAdaption() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", true);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", false);

    assertTrue(subscriptionService.isSubscriptionInUse());
  }

  @Test
  void subscriptionInUseShouldReturnTrueIfSubscriptionRequired() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", false);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", true);

    assertTrue(subscriptionService.isSubscriptionInUse());
  }

  @Test
  void subscriptionInUseShouldReturnTrueIfSubscriptionAdaptationAndRequired() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", true);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", true);

    assertTrue(subscriptionService.isSubscriptionInUse());
  }

  @Test
  void subscriptionInUseShouldReturnFalseIfNone() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", false);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", false);

    assertFalse(subscriptionService.isSubscriptionInUse());
  }

  @Test
  void subscriptionAdaptationAndNotRequiredShouldReturnTrueIfSubscriptionAdaptation() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", true);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", false);

    assertTrue(subscriptionService.isSubscriptionAdaptationAndNotRequired());
  }

  @Test
  void isSubscriptionAdaptationAndNotRequiredShouldReturnFalseIfSubscriptionRequired() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", false);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", true);

    assertFalse(subscriptionService.isSubscriptionAdaptationAndNotRequired());
  }

  @Test
  void isSubscriptionAdaptationAndNotRequiredShouldReturnFalseIfSubscriptionAdaptationAndRequired() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", true);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", true);

    assertFalse(subscriptionService.isSubscriptionAdaptationAndNotRequired());
  }

  @Test
  void isSubscriptionAdaptationAndNotRequiredShouldReturnFalseIfNone() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionAdaptation", false);
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", false);

    assertFalse(subscriptionService.isSubscriptionAdaptationAndNotRequired());
  }

  @Test
  void subscriptionRequiredReturnTrue() {
    ReflectionTestUtils.setField(subscriptionService, "subscriptionRequired", true);

    assertTrue(subscriptionService.isSubscriptionRequired());
  }

}
