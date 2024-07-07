import java.util.*;


class Card {
    protected List<String> list;
    public String get() {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public void delete(String a) {
        list.remove(a);
    }
}

class Rooms extends Card {
    public Rooms() {

        this.list = new ArrayList<>(Arrays.asList("Living Room", "Piano Room", "Greenhouse", "Study Room", "Billiard Room", "Bedroom", "Kitchen", "Dining Room", "Library"));

    }
}

class Characters extends Card {
    public Characters() {
        this.list = new ArrayList<>(Arrays.asList("Emma", "Liam", "Jack", "Sophia", "Emily", "Ella"));
    }
}

class Places extends Card {
    public Places() {
        this.list = new ArrayList<>(Arrays.asList("Under the Vase", "Hidden Drawer", "Behind the Picture", "Inside the Box", "Under the Table", "On Top of the Cupboard"));
    }
}

class Player {
    public List<String> cards;
    public int diceNumber;
    public String[] guess;
    public boolean dead;
    public int no;
    public int currentRoom;

    public Player(int number) {
        this.cards = new ArrayList<>();
        this.diceNumber = 0;
        this.guess = new String[3];
        this.no = number;
        this.dead = false;
        this.currentRoom = 0;
    }

    public void setCards(List<String> cards) {
        this.cards = cards;
    }

    public List<String> getCards() {
        return this.cards;
    }

    public int getDiceNumber() {
        return this.diceNumber;
    }


    public int rollDice() {
        Random rand = new Random();
        this.diceNumber = rand.nextInt(12) + 1;
        return diceNumber;
    }

    public void randomGuess(Rooms rooms, Characters characters, Places places) {
        this.guess[0] = characters.get();


        List<String> ar = new ArrayList<>();
        for (int i = 0; i < rooms.list.size(); i++) {
            if (diceNumber % 2 == 0 && i % 2 == 0) {
                ar.add(rooms.list.get(i));

            } else if (diceNumber % 2 != 0 && i % 2 != 0) {
                ar.add(rooms.list.get(i));
            }
        }
        System.out.println("Valid rooms based on dice: " + ar);
        Random rand = new Random();
        this.guess[1] = ar.get(rand.nextInt(ar.size()));
        this.guess[2] = places.get();
    }

    public String[] getGuess() {
        return this.guess;
    }

    public void setGuess(String character, String room, String place) {
        this.guess[0] = character;
        this.guess[1] = room;
        this.guess[2] = place;
    }
}

public class TreasureHuntGame {
    private List<Player> players;
    private Rooms rooms;
    private Characters characters;
    private Places places;
    private List<String> secret;


    public TreasureHuntGame(int numberOfPlayers) {
        this.players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            this.players.add(new Player(i));
        }
        this.rooms = new Rooms();
        this.characters = new Characters();
        this.places = new Places();
        this.secret = new ArrayList<>();
        setupGame();
    }

    private void setupGame() {
        Random rand = new Random();
        this.secret.add(characters.get());
        this.secret.add(rooms.get());
        this.secret.add(places.get());
        System.out.println("The final answer: " + secret);
        characters.delete(secret.get(0));
        rooms.delete(secret.get(1));
        places.delete(secret.get(2));

        System.out.println(rooms.list);

        List<String> remainingCards = new ArrayList<>();
        remainingCards.addAll(characters.list);
        remainingCards.addAll(rooms.list);
        remainingCards.addAll(places.list);
        Collections.shuffle(remainingCards);

        int numCards = remainingCards.size();
        int extraCards = numCards % players.size();
        int playerIndex = 0;

        for (int i = 0; i < numCards - extraCards; i++) {
            players.get(playerIndex).getCards().add(remainingCards.get(i));
            playerIndex = (playerIndex + 1) % players.size();

        }
        if (extraCards > 0) {
            List<String> extraCardsList = remainingCards.subList(numCards - extraCards, numCards);
            System.out.println("Extra cards: " + extraCardsList);
        }
    }

    public void playGame() {
        Scanner scanner = new Scanner(System.in);
        boolean gameEnded = false;
        for (Player player : players) {
            System.out.println("Cards of Player no. " + players.indexOf(player) + " " + player.cards);
        }
        while (!gameEnded) {
            for (Player player : players) {
                String[] usersGuess = new String[3];
                player.rollDice();
                System.out.println("Player no." + players.indexOf(player) + "'s dice: " + player.rollDice());

                int validMove;
                if (!player.dead) {
                    if (player.no == 0) {
                        System.out.println("Which room do you want to go to? (Enter room index): ");
                        validMove = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        player.randomGuess(rooms, characters, places);
                        validMove = rooms.list.indexOf(player.getGuess()[1]);
                    }

                    while (!(validMove % 2 == player.getDiceNumber() % 2) || validMove == player.currentRoom - 1 || validMove == player.currentRoom + 1) {
                        //System.out.println("Invalid move! Choose a valid room.");
                        if (player.no == 0) {
                            validMove = scanner.nextInt();
                            scanner.nextLine();
                        } else {
                            player.randomGuess(rooms, characters, places);
                            validMove = rooms.list.indexOf(player.getGuess()[1]);
                        }
                    }


                    player.currentRoom = validMove;
                    System.out.println("Player no." + player.no + " moved to " + rooms.list.get(validMove));
                }

                if (!player.dead) {

                    if (player.no != 0) {
                        player.randomGuess(rooms, characters, places);
                    } else {
                        System.out.println("Type your guess (Character, Room, Place): ");
                        for (int i = 0; i < 3; i++) {
                            if (i == 1) {
                                usersGuess[i] = scanner.nextLine();
                                usersGuess[i] = rooms.list.get(player.currentRoom);
                            } else {
                                usersGuess[i] = scanner.nextLine();
                            }
                        }
                        player.guess = usersGuess;
                    }
                }


                String[] guess = player.getGuess();
                System.out.println("Player's guess: " + Arrays.toString(guess));

                Player currPlayer = player;
                for (int i = 0; i < players.size(); i++) {
                    List<String> intersection = new ArrayList<>(Arrays.asList(currPlayer.getGuess()));
                    intersection.retainAll(players.get((players.indexOf(currPlayer) + 1) % (players.size())).getCards());


                    if (!intersection.isEmpty()) {
                        Random rand = new Random();
                        System.out.println("Player no." + ((players.indexOf(currPlayer) + 1) % players.size())
                                + " has this card: " + intersection.get(rand.nextInt(intersection.size())));
                        break;
                    } else {
                        currPlayer = players.get((players.indexOf(currPlayer) + 1) % (players.size() - 1));
                    }
                }
            }
            for (Player player : players) {
                String yesOrNo = "no";
                if (!player.dead) {
                    System.out.println("Player " + players.indexOf(player) + " Do you know the final answer? (yes, no)");
                    if (players.indexOf(player) == 0) {
                        yesOrNo = scanner.nextLine();
                    } else {
                        Random rand = new Random();
                        List<String> botSaysYesOrNo = new ArrayList<>();
                        botSaysYesOrNo.add("yes");
                        botSaysYesOrNo.add("no");
                        botSaysYesOrNo.add("no");
                        yesOrNo = botSaysYesOrNo.get(rand.nextInt(botSaysYesOrNo.size()));
                        System.out.println(yesOrNo);
                    }
                }
                if (yesOrNo.equals("yes")) {
                    if (Arrays.equals(player.getGuess(), secret.toArray())) {
                        System.out.println("Player " + players.indexOf(player) + " has guessed correctly! They win!");
                        gameEnded = true;
                        break;
                    } else {
                        System.out.println("Incorrect guess. Next player's turn.");
                        player.dead = true;
                    }
                }
            }
        }
    }

        public static void main (String[]args){
            System.out.println("Welcome to Treasure Hunt Game!");
            System.out.println("How many players do you want in the game? ( From 3 to 6 people can play )");
            boolean validInput = false;
            Scanner scanner = new Scanner(System.in);
            int playerNum = 0;

            while (!validInput) {
                try {
                    playerNum = scanner.nextInt();
                    if (playerNum >= 3 && playerNum <= 6) {
                        validInput = true;
                    } else {
                        System.out.println("Invalid number of players. Please choose between 3 to 6 players.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number between 3 and 6.");
                    scanner.next();
                }
            }
            TreasureHuntGame game = new TreasureHuntGame(playerNum);
            game.playGame();
        }
    }



