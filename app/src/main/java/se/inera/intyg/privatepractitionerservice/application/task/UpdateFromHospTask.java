package se.inera.intyg.privatepractitionerservice.application.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import se.inera.intyg.privatepractitionerservice.application.privatepractitioner.service.UpdateHospService;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcCloseableMap;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcHelper;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.MdcLogConstants;
import se.inera.intyg.privatepractitionerservice.infrastructure.logging.PerformanceLogging;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateFromHospTask {

  private static final String TASK_NAME = "UpdateFromHospTask.run";
  private static final String LOCK_AT_MOST = "PT50S";
  private static final String LOCK_AT_LEAST = "PT50S";

  private final UpdateHospService updateHospService;
  private final MdcHelper mdcHelper;

  @Scheduled(cron = "${hosp.update.cron}")
  @SchedulerLock(name = TASK_NAME, lockAtLeastFor = LOCK_AT_LEAST, lockAtMostFor = LOCK_AT_MOST)
  @PerformanceLogging(eventAction = "update-from-hosp", eventType = MdcLogConstants.EVENT_TYPE_CHANGE)
  public void update() {
    try (MdcCloseableMap mdc =
        MdcCloseableMap.builder()
            .put(MdcLogConstants.TRACE_ID_KEY, mdcHelper.traceId())
            .put(MdcLogConstants.SPAN_ID_KEY, mdcHelper.spanId())
            .build()
    ) {
      updateHospService.update();
    } catch (Exception ex) {
      log.error("Error occurred while updating from HOSP", ex);
    }
  }
}
