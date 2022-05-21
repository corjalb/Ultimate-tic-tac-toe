
var turns = [['#','#','#'],['#','#','#'],['#','#','#']];
var turn = "X";
var gameOn = false;


function playerTurn (turn, id){
    if(gameOn) {
        var spotTaken = $("#"+id).text();
        if (spotTaken ==="#"){
            makeAMove(playerType, id.split('_')[0],id.split('_')[1],id.split('_')[2]);
        }
    }

}

function makeAMove(type,subBoardNumber, xCoordinate, yCoordinate) {
    $.ajax({
                url: url + "/game/gameplay",
                type: 'POST',
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify({
                    "type" : type,
                    "coordinateX" : xCoordinate,
                    "coordinateY" : yCoordinate,
                    "gameId" : gameId,
                    "subBoardNumber" : subBoardNumber
                }),
                success: function(data) {
                    gameOn = false;
                    displayResponse(data);
                },
                error: function(error) {
                    console.log(error);
                }
            })
}

function displayResponse(data) {
    if(data.player2 == null) return;
    let board = data.board;
    turn = data.turn;
    for(let i = 0; i < board.length; i++) {
      let subBoard = board[i].subBoard;
      let status = board[i].status;
      if(status == "WON") {
        $('#subBoard' + i).addClass(board[i].winner);
      }
      if(status == "ACTIVE") {
        $('#subBoard' + i).addClass('active');
      } else {
        $('#subBoard' + i).removeClass('active');
      }
      for(let j = 0; j < subBoard.length; j++) {
        for(let k = 0; k < subBoard[j].length; k++) {
          let id = i + "_" + j + "_" + k;
          if(subBoard[j][k] === 1) {
            turns[j][k] = 'X';
            $('#' + id).text('X');
            $('#' + id).addClass('X')
          } else if(subBoard[j][k] === 2) {
            turns[j][k] = 'O';
            $('#' + id).text('O');
            $('#' + id).addClass('O')
          }
        }
      }
    }

    if(data.winner != null) {
        alert("Winner is " + data.winner + "!");
    }
    gameOn = true;
}


$(".tic").click(function(){
    var slot = $(this).attr('id');
    if(playerType == turn && $(this).parent().hasClass('active')) playerTurn(turn,slot);
});

function reset(){
  turns = ["#","#","#","#","#","#","+","#"];
  $(".tic").text("#");
}

$("#reset").click(function(){
  reset();
});
