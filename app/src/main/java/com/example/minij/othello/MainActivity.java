package com.example.minij.othello;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.minij.othello.R.*;
import static com.example.minij.othello.R.layout.*;
import java.util.*;

//TODO: Option for undoing previous move
//TODO: Computer, min max algorithm use
//TODO: Handle both cases, multiplayer and singleplayer
//TODO: Make another activity for asking multiplayer or singleplayer
//if multiplayer: player1 =0; player2=1;
//if singleplayer: computer=0; user=1; user starts. TODO: make a separate starting function for the game in case of singleplayer
//computer tries to maximise score, player tries to minimise score. if score is <0 player is winning, if score>0 computer is winning

//User:1    Computer:0

public class MainActivity extends AppCompatActivity {

    int player = 0;
    int gameState=1;                             //tells whether game is currently ongoing or stopped
    int array[][] = new int[8][8];              //gameboard
    ImageView arr[]=new ImageView[64];
    int[] xdirection = new int[]{0, -1, -1, -1, 0, 1, 1, 1};
    int[] ydirection = new int[]{-1, -1, 0, 1, 1, 1, 0, -1};
    //-1:unfilled, 0:black, 1:white

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                array[i][j] = -1;
            }
        }
        array[3][3] = 1;
        array[3][4] = 0;
        array[4][3] = 0;
        array[4][4] = 1;
//        try {
//            GridLayout gridLayout = (GridLayout) findViewById(id.grid);        //why nullpointerexception here
//            for (int i = 0; i < gridLayout.getChildCount(); i++) {
//                arr[i] = (ImageView) gridLayout.getChildAt(i);
//            }
//            ((ImageView) (arr[27])).setImageResource(drawable.newwhite);
//            ((ImageView) (arr[28])).setImageResource(drawable.newblack);
//            ((ImageView) (arr[35])).setImageResource(drawable.newblack);
//            ((ImageView) (arr[36])).setImageResource(drawable.newwhite);
//        }
//        catch (NullPointerException e)
//        {
//            Log.e("Error", "in here");
//        }
    }

    public void chance(View view) {
        ImageView iv = (ImageView) view;
        String id = iv.getTag().toString();
        String address = id.substring(9);
//        String ourId="";
//        ourId=view.getResources().getResourceEntryName(view.getId());
        if (iv.getDrawable() != null)                                     //already move has been made in this position
        {         //toast message: invalid move
            Toast.makeText(MainActivity.this, "Invalid move", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gameState==1)
        {
            if (isMoveValid(address.charAt(0) - '0', address.charAt(1) - '0', player))
            {      //flipping of discs is done if move is valid
                if (player == 0) {
                    iv.setImageResource(drawable.newblack);
                    array[address.charAt(0) - '0'][address.charAt(1) - '0'] = 0;    //change in array also
                    player = 1;                                                     //chance switches to other player

                } else {
                    iv.setImageResource(drawable.newwhite);
                    array[address.charAt(0) - '0'][address.charAt(1) - '0'] = 1;    //change in array also
                    player = 0;
                }                                                              //current player's chance complete, player value changes
                setCount();

                 if (playerCantMoveAnywhere(player)) {
                    if (playerCantMoveAnywhere(opposite(player))) {
                        //game over
                        winningAction();
                    } else {
                        String ans = "white";
                        if (player == 0)
                            ans = "black";
                        Toast.makeText(MainActivity.this, ans + " has no possible moves, next player's turn", Toast.LENGTH_LONG).show();
                        //toast message that player has no possible moves, hence next player should play his turn
                        player = opposite(player);
                    }
                }
            }
            else
            {
                Toast.makeText(MainActivity.this, "Invalid move", Toast.LENGTH_SHORT).show();

                if (playerCantMoveAnywhere(player))
                {
                    if (playerCantMoveAnywhere(opposite(player)))
                    {
                        //game over
                        winningAction();
                    }
                    else
                    {
                        String ans = "white";
                        if (player == 0)
                            ans = "black";
                        Toast.makeText(MainActivity.this, ans + " has no possible moves, next player's turn", Toast.LENGTH_LONG).show();
                        //toast message that player has no possible moves, hence next player should play his turn
                        player = opposite(player);
                    }
                }
            }

            if (isBoardFull())
            {
                winningAction();
            }

            if (playerCantMoveAnywhere(player))
            {
                if (playerCantMoveAnywhere(opposite(player)))
                {
                    winningAction();
                }
                else
                {
                    String ans = "white";
                    if (player == 0)
                        ans = "black";
                    Toast.makeText(MainActivity.this, ans + " has no possible moves, next player's turn", Toast.LENGTH_LONG).show();
                    //toast message that player has no possible moves, hence next player should play his turn
                    player = opposite(player);
                }
            }
        }
    }

    private void winningAction()
    {
        String win = "Black wins!";
        int winner = decideWinner();
        if (winner == 0) {
            //print black wins
        } else if (winner == 1) {
            //print white wins
            win = "White wins!";
        } else {
            //print draw
            win = "It's a draw!";
        }
        TextView winnermessage = (TextView) findViewById(R.id.GameEndingmessage);
        winnermessage.setText(win);
        gameState=0;
        LinearLayout linearlayout = (LinearLayout) findViewById(R.id.message);
        linearlayout.setVisibility(View.VISIBLE);
    }

    private void setCount()
    {
        Position p=countOfBlackAndWhite();
        int b=p.i;
        int w=p.j;
        TextView black= (TextView) findViewById(id.blackdiscs);
        TextView white= (TextView) findViewById(id.whitediscs);
        black.setText("Black : "+Integer.toString(b));
        white.setText("White : "+Integer.toString(w));
    }

    private boolean isBoardFull()             //checks if there are no more empty spaces on board
    {
        boolean boardFull=true;
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                if (array[i][j]==-1)
                    boardFull=false;
            }
        }
        return boardFull;
    }

    private int decideWinner()               //counting of discs to decide winner after no valid moves left
    {
        Position p=countOfBlackAndWhite();
        int white=p.j;
        int black=p.i;
        if (black>white)
            return 0;
        else if (white>black)
            return 1;
        return 2;
    }

    private Position countOfBlackAndWhite()
    {
        int b=0;
        int w=0;
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                if (array[i][j]==0)
                    b++;
                if (array[i][j]==1)
                    w++;
            }
        }
        return new Position(b,w);
    }

    private boolean playerCantMoveAnywhere(int playertemp)      //checks of there are any valid moves possible for playertemp
    {
        boolean noMovesPossible=true;
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                ImageView temp=returnImageView(i,j);
                if (temp != null)
                {
                    if (temp.getDrawable()!=null)
                        continue;
                }
                if (array[i][j]!=-1) {
                    continue;
                }
                if (canMakeMoveHere(i,j,playertemp))
                    noMovesPossible=false;
            }
        }
        return noMovesPossible;
    }

    public ArrayList<Position> allPossibleMoves(int currentplayer)
    {
        ArrayList<Position> possibleMoves=new ArrayList<Position>();
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                if (canMakeMoveHere(i,j,currentplayer))
                {
                    Position p=new Position();
                    p.i=i;
                    p.j=j;
                    possibleMoves.add(p);
                }
            }
        }
        return possibleMoves;
    }

    private boolean canMakeMoveHere(int i, int j, int playertemp)       //checks if playertemp can make a move at i,j
    {
//        GridLayout grid=(GridLayout) findViewById(id.gridLayout);
//            ImageView temp = (ImageView) grid.getChildAt(i * 8 + j);
//            if (temp != null)
//            {
//                if (temp.getDrawable()!=null)
//                    return false;
//            }
        boolean moveValid = false;
        int count=0;
        for (int m = 0; m < xdirection.length; m++) {
            int x = xdirection[m];
            int y = ydirection[m];
            int currentx = i + x;
            int currenty = j + y;
            int stop = 0;
            int check1 = 0;
            int check2 = 0;
            int countOfOppColorFound = 0;
            while (((-1 < currentx) && currentx < 8) && ((-1 < currenty) && currenty < 8)) {
                if (array[currentx][currenty] == -1) {
                    check1 = 1;           //implies board is not full in that line of direction
                    break;
                }
                if (array[currentx][currenty] == playertemp && countOfOppColorFound == 0)  //to check for pattern: BB...
                {
                    check2 = 1;
                    break;
                } else if (array[currentx][currenty] != playertemp) {
                    countOfOppColorFound = countOfOppColorFound + 1;
                    currentx += x;
                    currenty += y;
                } else if (array[currentx][currenty] == playertemp && countOfOppColorFound != 0) {
                    stop = 1;               //to check for type of pattern: eg- BWWWW
                    break;
                }
            }
            if (stop == 1 && countOfOppColorFound > 0) {
                moveValid = true;
                count=count+countOfOppColorFound;
            }
        }
        if (count==0)
            moveValid=false;
        return moveValid;
    }

    private ImageView returnImageView(int i, int j)                    //returns the ImageView of type View at i,j position on board
    {
        String s = "imageView" + String.valueOf(i) + String.valueOf(j);
        //int add=Integer.parseInt(s);
        ImageView imageview = null;
        imageview = findViewById(R.id.imageView00);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }
        imageview = findViewById(R.id.imageView01);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView02);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView03);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(id.imageView04);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView05);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView06);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView07);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView10);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView11);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView12);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView13);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView14);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView15);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView16);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView17);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView20);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView21);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView22);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView23);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView24);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView25);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView26);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView27);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView30);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView31);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView32);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView33);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView34);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView35);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView36);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView37);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView40);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView41);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView42);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView43);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView44);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView45);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView46);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView47);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView50);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView51);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView52);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView53);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView54);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView55);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView56);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView57);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView60);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView61);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView62);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView63);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView64);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView65);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView66);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView67);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView70);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView71);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView72);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView73);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView74);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView75);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView76);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }

        imageview = findViewById(R.id.imageView77);
        if (imageview != null) {
            if (s.equals(imageview.getTag().toString())) {
                return imageview;
            }
        }
        return null;
    }

    private void flip(int i, int j) {                                //flips the imageview at i,j position on board
        ImageView imageview = null;
//        GridLayout grid = (GridLayout) findViewById(id.gridLayout);
//        ImageView temp=null;
//        if (grid != null) {
//            temp = (ImageView) grid.getChildAt(i * 8 + j);
//        }
//        if (temp!=null) {
//            if (array[i][j] == 0) {
//                temp.setImageResource(drawable.newwhite);
//                //change in array also
//                array[i][j] = 1;
//            } else {
//                temp.setImageResource(drawable.newblack);
//                array[i][j] = 0;
//            }
//        }
//
//        return;
        imageview = returnImageView(i, j);
        if (imageview != null) {
            if (array[i][j] == 0) {
                imageview.setImageResource(drawable.newwhite);
                //change in array also
                array[i][j] = 1;
            } else {
                imageview.setImageResource(drawable.newblack);
                array[i][j] = 0;
            }
        }
    }

    private boolean isMoveValid(int i, int j, int tempplayer)          //returns if move is valid   // if valid, then makes the required flips
    {
        boolean moveValid = false;
        for (int m = 0; m < xdirection.length; m++) {
            final int x = xdirection[m];
            final int y = ydirection[m];
            int currentx = i + x;
            int currenty = j + y;
            int stop = 0;
            int check1 = 0;
            int check2 = 0;
            int countOfOppColorFound = 0;
            while (((-1 < currentx) && currentx < 8) && ((-1 < currenty) && currenty < 8))
          {
                if (array[currentx][currenty] == -1)
                {
                    check1 = 1;
                    break;
                }
                if (array[currentx][currenty] == tempplayer && countOfOppColorFound == 0)  //to check for pattern: BB...
                {
                    check2 = 1;
                    break;
                } else if (array[currentx][currenty] != tempplayer)
                {
                    countOfOppColorFound=countOfOppColorFound+1;
                    currentx += x;
                    currenty += y;
                } else if (array[currentx][currenty] == tempplayer && countOfOppColorFound != 0)
                {
                    stop = 1;               //to check for type of pattern: eg- BWWWW
                    break;
                }
           }
            if (stop == 1 && countOfOppColorFound > 0)
                moveValid = true;
            boolean tempMoveValid = moveValid;
            if (stop == 0 || countOfOppColorFound==0 || check2==1)
                tempMoveValid = false;
            if (tempMoveValid) {// if successfully found pattern of type: B..W..B
                currentx = i + x;
                currenty = j + y;
                while ((-1 < currentx) && (currentx < 8) && (-1 < currenty) && (currenty < 8)) {
                    try {
                        if (array[currentx][currenty] == -1)
                            break;
                        else if (array[currentx][currenty] != tempplayer) {
                            flip(currentx, currenty);
                            currentx += x;
                            currenty += y;
                        } else
                            break;
                    } catch (Exception e) {
                        Log.e("A", "Error here");
                        e.printStackTrace();
                    }
                }
            }
        }
        return moveValid;
    }

    private int opposite(int player)
    {
        if (player==0)
            return 1;
        return 0;
    }

    public void newGame(View view)                         //starts a new game
    {
        player = 0;
        gameState=1;     //game is active
        //-1:unfilled, 0:black, 1:white
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                array[i][j] = -1;
            }
        }
        array[3][3] = 1;
        array[3][4] = 0;
        array[4][3] = 0;
        array[4][4] = 1;
        //remove images from sources
        setCount();
        for (int i=0;i<8;i++)
        {
            for (int j=0;j<8;j++)
            {
                ImageView image=returnImageView(i,j);
                if (image!=null) {
                    image.setImageResource(0);
                }
            }
        }
//        for (int i=0;i<64;i++)
//        {
//            arr[i].setImageResource(0);
//        }
//
//        GridLayout gridLayout=(GridLayout) findViewById(id.gridLayout);
//        for (int i=0;i<gridLayout.getChildCount();i++) {
//            arr[i] = (ImageView) gridLayout.getChildAt(i);
//        }
//        (arr[27]).setImageResource(drawable.newwhite);
//        arr[28].setImageResource(drawable.newblack);
//        arr[35].setImageResource(drawable.newblack);
//        arr[36].setImageResource(drawable.newwhite);

        ImageView image=returnImageView(3,3);
        if (image!=null) {
            image.setImageResource(drawable.newwhite);
        }
        image=returnImageView(3,4);
        if (image!=null) {
            image.setImageResource(drawable.newblack);
        }
        image=returnImageView(4,3);
        if (image!=null) {
            image.setImageResource(drawable.newblack);
        }
        image=returnImageView(4,4);
        if (image!=null) {
            image.setImageResource(drawable.newwhite);
        }
        LinearLayout linearlayout = findViewById(R.id.message);
        linearlayout.setVisibility(View.INVISIBLE);
    }
}