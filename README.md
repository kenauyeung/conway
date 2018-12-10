# Conway's Game of Life
Implement Conway's Game of Life using Spring Boot + SockJS.

## Development
### Server
Cell transition can wrap around the universe(can be considered like sphere), rather than discard once the cell pass the edge of universe.

#### Universe Transition
1. Get 8 neighbour cells around the tested cell, if neighbour cell x/y outside of universe edge(less than 0 or greater than universe size) than wrap around
2. Test against game rules
3. Move to next cell in the universe
4. Update universe view
5. Push only the updated cells to subscribed users

Currently method will test every single cells in the universe.

### Front-end
Each `Message` from server contains a unique message id. 

Should never process message id lower than previous one, this can cause incorrect cell state updates.

Following are the steps connecting to the game
1. Connects to server using websocket
2. Subscribe to `/topic/cells` for universe update data
3. Request `/user/queue/initialize` data, if 10 update data received and initialization data not received than it will disconnect
4. Once initialize data process completed, start process update data
5. Discard any update message that its id is less than previous process id

Reason for subscribe update first is to ensure no update message missed during retrieve initialization data.

For Example,
Initialization message contain message id 1 but due to unforeseeable delay, the message arrive after message id 2.
At the front-end side, they only able to process message id 1 and missed message id 2.

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

