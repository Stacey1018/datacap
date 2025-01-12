package io.edurt.datacap.server.configure;

import com.google.inject.Injector;
import io.edurt.datacap.schedule.ScheduledCronRegistrar;
import io.edurt.datacap.server.scheduled.source.CheckScheduledRunnable;
import io.edurt.datacap.server.scheduled.source.SyncMetadataScheduledRunnable;
import io.edurt.datacap.service.repository.ScheduledHistoryRepository;
import io.edurt.datacap.service.repository.ScheduledRepository;
import io.edurt.datacap.service.repository.SourceRepository;
import io.edurt.datacap.service.repository.TemplateSqlRepository;
import io.edurt.datacap.service.repository.metadata.ColumnRepository;
import io.edurt.datacap.service.repository.metadata.DatabaseRepository;
import io.edurt.datacap.service.repository.metadata.TableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ScheduleRunnerConfigure
        implements CommandLineRunner
{
    private final Injector injector;
    private final ScheduledRepository scheduledRepository;
    private final SourceRepository sourceRepository;
    private final DatabaseRepository databaseHandler;
    private final TableRepository tableHandler;
    private final ColumnRepository columnHandler;
    private final ScheduledHistoryRepository scheduledHistoryHandler;
    private final TemplateSqlRepository templateSqlRepository;
    private final ScheduledCronRegistrar scheduledCronRegistrar;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ScheduleRunnerConfigure(Injector injector, ScheduledRepository scheduledRepository, SourceRepository sourceRepository, DatabaseRepository databaseHandler, TableRepository tableHandler, ColumnRepository columnHandler, ScheduledHistoryRepository scheduledHistoryHandler, TemplateSqlRepository templateSqlRepository, ScheduledCronRegistrar scheduledCronRegistrar)
    {
        this.injector = injector;
        this.scheduledRepository = scheduledRepository;
        this.sourceRepository = sourceRepository;
        this.databaseHandler = databaseHandler;
        this.tableHandler = tableHandler;
        this.columnHandler = columnHandler;
        this.scheduledHistoryHandler = scheduledHistoryHandler;
        this.templateSqlRepository = templateSqlRepository;
        this.scheduledCronRegistrar = scheduledCronRegistrar;
    }

    @Override
    public void run(String... args)
    {
        this.scheduledRepository.findAllByActiveIsTrueAndIsSystemIsTrue()
                .forEach(task -> {
                    log.info("Add new task [ {} ] to scheduler", task.getName());
                    switch (task.getType()) {
                        case SOURCE_SYNCHRONIZE:
                            SyncMetadataScheduledRunnable syncMetadataScheduledRunnable = new SyncMetadataScheduledRunnable(task.getName(), injector, sourceRepository, databaseHandler, tableHandler, columnHandler, templateSqlRepository, scheduledHistoryHandler, task);
                            this.scheduledCronRegistrar.addCronTask(syncMetadataScheduledRunnable, task.getExpression());
                            executorService.submit(() -> syncMetadataScheduledRunnable.run());
                            break;
                        case SOURCE_CHECK:
                            CheckScheduledRunnable checkScheduledRunnable = new CheckScheduledRunnable(task.getName(), this.injector, this.sourceRepository);
                            this.scheduledCronRegistrar.addCronTask(checkScheduledRunnable, task.getExpression());
                            executorService.submit(() -> checkScheduledRunnable.run());
                            break;
                        default:
                            log.warn("Unsupported task type [ {} ]", task.getType());
                    }
                });
    }
}
