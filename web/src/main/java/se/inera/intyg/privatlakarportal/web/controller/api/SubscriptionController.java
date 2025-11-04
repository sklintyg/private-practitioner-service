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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.privatlakarportal.logging.MdcLogConstants;
import se.inera.intyg.privatlakarportal.logging.PerformanceLogging;
import se.inera.intyg.privatlakarportal.service.SubscriptionService;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.SubscriptionResponse;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

  @Autowired
  private SubscriptionService subscriptionService;

  @GetMapping("/state")
  @PerformanceLogging(eventAction = "get-subscription-state", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public SubscriptionResponse getSubscriptionState() {
    return new SubscriptionResponse(subscriptionService.isSubscriptionInUse());
  }
}
