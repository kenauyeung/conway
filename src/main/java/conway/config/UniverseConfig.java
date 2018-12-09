package conway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import conway.game.logic.model.Universe;

@Configuration
public class UniverseConfig {

	@Value("${universe.range.x}")
	private int universeX;

	@Value("${universe.range.y}")
	private int universeY;

	@Bean
	public Universe CreateWorld() throws Exception {
		return new Universe(universeX, universeY);
	}
}
