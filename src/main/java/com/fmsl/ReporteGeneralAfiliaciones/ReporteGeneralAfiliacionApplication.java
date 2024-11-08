package com.gracaconsultores.ReporteGeneralAfiliaciones;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class ReporteGeneralAfiliacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReporteGeneralAfiliacionApplication.class, args);
	}

	@Bean("asyncTrackLog")
	public TaskExecutor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(30);
		executor.setQueueCapacity(10);
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.setThreadNamePrefix("LOG-ASYNC");
		executor.initialize();
		return executor;
	}

}

//	@Bean
//	public Docket api() {
//		log.info("-- ActualizaAfiliacionApplication.api: ");
//		return new Docket(DocumentationType.SWAGGER_2)
//				.apiInfo(new ApiInfo("Service Model - GRACA", "Microservice for BDV - GRACA", "0.1", "termsOfServiceUrl", "luischirino2970@gmail.com", "", "licenseUrl"))
//				.select()
//				.apis(RequestHandlerSelectors.any())
//				.paths(PathSelectors.any())
//				.paths(Predicates.not(PathSelectors.regex("/error.*")))
//				.build();
//	}
//}
