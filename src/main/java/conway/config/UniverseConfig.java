package conway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import conway.game.logic.model.Universe;

@Configuration
public class UniverseConfig {
	
	private int worldX = 20;
	private int worldY = 20;
	
	@Bean
	public Universe CreateWorld() throws Exception {
		return new Universe(worldX, worldY);
	}
}
