# Conway's Game of Life
Implement Conway's Game of Life using Spring Boot + SockJS.

## Development
### Server
1. `UniverseProcessor.java` responsible for handling the game universe
2. `WebSocketServiceController.java` responsible for handling request from front-end client
3. `Message.java` contains cells information such as cells update and/or delete

Cell transition can wrap around the universe(can be considered like sphere), rather than discard once the cell pass the edge of universe.

### Front-end
Each `Message` from server contains a unique message id. 

Should never process message id lower than previous one, this can cause incorrect cell state updates.

## Requirements
1. Java 1.8+
2. Access to Maven repository

## Configurations
Server configuration is located within application.yml

Parameter | Description | Default Value
--- | --- | ---
server.port | Default web server port | 80
universe.range.x | Number of horizontal cells | 20
universe.range.y | Number of vertical cells | 20
universe.processor.pulse | Time between each transition in milliseconds | 1000

## Enhancement
Currently, all cells will be calculated in the transition. Next version should reduce the number of calculation by only calculate cells around live cells, rather than all cells in the universe.

The above improvement should work based on assumption that reviving neighbour will not lead to revive neighbour's neighbour.
