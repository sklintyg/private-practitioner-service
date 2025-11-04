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

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkService;
import se.inera.intyg.privatlakarportal.common.integration.kodverk.Befattningar;
import se.inera.intyg.privatlakarportal.common.integration.kodverk.Vardformer;
import se.inera.intyg.privatlakarportal.common.integration.kodverk.Verksamhetstyper;
import se.inera.intyg.privatlakarportal.logging.MdcLogConstants;
import se.inera.intyg.privatlakarportal.logging.PerformanceLogging;
import se.inera.intyg.privatlakarportal.web.controller.api.dto.GetConfigResponse;

/**
 * Created by pebe on 2015-08-28.
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

  @Autowired
  private Environment env;

  @Autowired
  private DynamicLinkService dynamicLinkService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  @PerformanceLogging(eventAction = "get-config", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public GetConfigResponse getConfig() {
    return new GetConfigResponse(
        env.getProperty("webcert.host.url"),
        env.getProperty("webcert.start.url"),
        Befattningar.getBefattningar(),
        Vardformer.getVardformer(),
        Verksamhetstyper.getVerksamhetstyper());
  }

  @RequestMapping(value = "/links", method = RequestMethod.GET, produces = "application/json")
  @PerformanceLogging(eventAction = "get-links", eventType = MdcLogConstants.EVENT_TYPE_ACCESSED)
  public Map<String, DynamicLink> getLinks() {
    return dynamicLinkService.getAllAsMap();
  }
}
