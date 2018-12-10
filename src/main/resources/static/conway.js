var conway = {
    stompClient: null,
    gameData: [],
    runDataProcess: false,
    lastDataId: 0,
    pattern : {
        single: [{x:0,y:0}],
        block: [{x:0,y:0},{x:1,y:0},{x:0,y:1},{x:1,y:1}],
        beehive: [{x:1,y:0},{x:2,y:0},{x:0,y:1},{x:3,y:1},{x:1,y:2},{x:2,y:2}],
        loaf: [{x:1,y:0},{x:2,y:0},{x:0,y:1},{x:3,y:1},{x:1,y:2},{x:3,y:2},{x:2,y:3}],
        beacon: [{x:0,y:0},{x:1,y:0},{x:0,y:1},{x:1,y:1},{x:2,y:2},{x:3,y:2},{x:2,y:3},{x:3,y:3}],
        glider : [{x:1,y:0}, {x:2,y:1},{x:0,y:2},{x:1,y:2},{x:2,y:2}],
    },
    onConnected: function (connected) {
        console.log("Connected:", connected);
    },
    onDataInitialized: function() {
        console.log("Data initialized");
    },
    connect: function () {
        var socket = new SockJS('/register-websocket');

        $('#drawArea').html("Connecting to server...");
        conway.reset();

        conway.stompClient = Stomp.over(socket);
        conway.stompClient.debug = null; // disable logging
        conway.stompClient.connect({}, function (frame) {
            conway.onConnected(true);
            console.log('Connected: ' + frame);

            var initSubId = 'initial-subscription-id-' + new Date().getTime();
            var cellSubId = 'cell-subscription-id-' + new Date().getTime();

            // must subscribe this to ensure no update message missed before the initial data arrive
            conway.stompClient.subscribe('/topic/cells', function (message) {
                if(!conway.runDataProcess && conway.gameData.length > 10){
                    conway.stompClient.unsubscribe(cellSubId);
                    alert("Initialzation failed, please connect again");
                    conway.disconnect();
                }else{
                    conway.gameData.push(JSON.parse(message.body));
                }
            }, {id: cellSubId});

            conway.stompClient.subscribe('/user/queue/initialize', function (message) {
                var jsonObj = JSON.parse(message.body);
                $('.user-cell-color').css('background-color', 'rgb(' + jsonObj.color[0] + ',' + jsonObj.color[1] + ',' + jsonObj.color[2] + ')');
                conway.draw.createGrid(jsonObj.initialMessage.xRng, jsonObj.initialMessage.yRng);
                conway.range = {x:jsonObj.initialMessage.xRng, y:jsonObj.initialMessage.yRng};
                conway.stompClient.unsubscribe(initSubId);
                conway.dataParser(jsonObj.initialMessage.data.create);
                conway.lastDataId = jsonObj.initialMessage.id;
                conway.runDataProcess = true;
                conway.dataProcessor();
                conway.onDataInitialized();
            }, {id: initSubId});

            conway.stompClient.send("/app/initialize");
        }, conway.disconnect);
    },
    reset: function(){
        conway.runDataProcess = false;
        conway.gameData = new Array();
        conway.lastDataId = 0;
    },
    disconnect: function () {
        if (conway.stompClient !== null) {
            conway.stompClient.disconnect();
        }
        conway.reset();
        conway.onConnected(false);
        console.log("Disconnected");
    },
    addCells: function(name, offsetX=0, offsetY=0){
        var patternObj = conway.pattern[name];
        if(patternObj){
            var d = [];
            for(i =0; i < patternObj.length; i++){
                var xVal = (parseInt(offsetX)+patternObj[i].x) % conway.range.x; 
                var yVal = (parseInt(offsetY)+patternObj[i].y) % conway.range.y;
                d.push({x:xVal, y:yVal});
            }
            conway.send("/app/addcells", {}, JSON.stringify(d));
        }
    },
    send: function (destination, headers = {}, body = '') {
        conway.stompClient.send(destination, headers, body);
    },
    dataProcessor: function () {
        if (conway.runDataProcess) {

            while (conway.gameData.length > 0) {
                var dataMessage = conway.gameData[0];
                if (dataMessage.id > conway.lastDataId) {
                    conway.dataParser(dataMessage.data.delete, true);
                    conway.dataParser(dataMessage.data.create);
                    conway.lastDataId = dataMessage.id;
                }
                conway.gameData.shift();
            }
            setTimeout(conway.dataProcessor, 500);
        }
    },
    dataParser: function (data, isDel) {
        if (data && data.length) {
            for (i = 0; i < data.length; i++) {
                conway.draw.updateCell(data[i].x, data[i].y, data[i].rgb && !isDel ? 'rgb(' + data[i].rgb[0] + ',' + data[i].rgb[1] + ',' + data[i].rgb[2] + ')' : undefined);
            }
        }
    },
    draw: {
        createGrid: function (x, y) {
            var tableStr = "<table id='conway_table'>";
            for (i = 0; i < y; i++) {
                tableStr += "<tr>";
                for (j = 0; j < x; j++) {
                    tableStr += "<td id='" + j + "_" + i + "_cell' class='conway-cell'/>"
                }
                tableStr += "</tr>";
            }

            tableStr += "</table>";
            $('#drawArea').html(tableStr);
        },
        updateCell: function (x, y, color) {
            if (!color) color = "#fff";
            $("#conway_table #" + x + "_" + y + "_cell").css("background-color", color);
        }
    }
};
