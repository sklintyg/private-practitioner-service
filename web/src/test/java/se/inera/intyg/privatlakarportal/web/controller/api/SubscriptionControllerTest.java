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
package se.inera.intyg.privatlakarportal.web.controller.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.privatlakarportal.service.SubscriptionService;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionControllerTest {

  @Mock
  private SubscriptionService subscriptionService;

  @InjectMocks
  private SubscriptionController subscriptionController = new SubscriptionController();

  @Test
  public void testGetSubscriptionState() {
    when(subscriptionService.isSubscriptionInUse()).thenReturn(true);

    var subscriptionResponse = subscriptionController.getSubscriptionState();

    verify(subscriptionService).isSubscriptionInUse();
    assertTrue(subscriptionResponse.isSubscriptionInUse());
  }

}
