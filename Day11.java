package Day11_Part1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Day11 {

    public static class Item {
        int worryLevel;

        public Item(int worryLevel) {
            this.worryLevel = worryLevel;
        }

        public int getWorryLevel() {
            return this.worryLevel;
        }

        public void setWorryLevel(int w) {
            this.worryLevel = w;
        }

        public void lowerWorryLevel() {
            double d = this.worryLevel / (double) 3;
            this.worryLevel = (int) Math.floor(d);
        }

    }

    public static class Monkey {
        int monkeyNumber;
        long numberOfInspectionsDone;
        ArrayList<Item> itemsList;
        Operation operation;
        Operation testOperation;

        public Monkey(int monkeyNumber) {
            this.monkeyNumber = monkeyNumber;
            this.numberOfInspectionsDone = 0;
            this.itemsList = new ArrayList<Item>();
        }

        public int getMonkeyNumber() {
            return this.monkeyNumber;
        }

        public long getNumberOfInspectionsDone() {
            return this.numberOfInspectionsDone;
        }

        public int getNumberOfItems() {
            return this.itemsList.size();
        }

        public Item getMonkeyItem(int index) {
            return this.itemsList.get(index);
        }

        public int getMonkeyItemWorryLevel(int itemIndex) {
            return this.itemsList.get(itemIndex).getWorryLevel();
        }

        public void incNumberOfInspectionsDone() {
            this.numberOfInspectionsDone++;
        }

        public void addItemToMonkey(int worryLevel) {
            Item item = new Item(worryLevel);
            this.itemsList.add(item);
        }

        public boolean hasItems() {
            return this.itemsList.size() > 0;
        }

        public void clearItemsList() {
            this.itemsList.clear();
        }

        public void setMonkeyItemWorryLevel(int itemIndex, int newWorryLevel) {
            this.getMonkeyItem(itemIndex).setWorryLevel(newWorryLevel);
        }

        public void setOperation(String operationString) {
            String[] parts = operationString.split(" ");
            if (parts[7].equals("old")) {
                this.operation = old -> old * old;
            } else {
                var value = isNumeric(parts[7]) ? Integer.parseInt(parts[7]) : 0; // using var because of lambda
                switch (parts[6]) {
                    case "+":
                        this.operation = old -> old + value;
                        break;
                    case "-":
                        this.operation = old -> old - value;
                        break;
                    case "*":
                        this.operation = old -> old * value;
                        break;
                    case "/":
                        this.operation = old -> old / value;
                        break;
                }

            }
        }

        public int performOperation(int itemIndex) {
            int itemWorryLevel = this.getMonkeyItemWorryLevel(itemIndex);
            int newWorryLevel = this.operation.apply(itemWorryLevel);
            return newWorryLevel;
        }

        public void lowerItemWorryLevel(int itemIndex) {
            this.itemsList.get(itemIndex).lowerWorryLevel();
        }

        public void setTestOperation(String[] testStrings) {
            String testOperationStr = testStrings[0];
            String[] tokens = testOperationStr.split(" ");
            int divisionNum = Integer.parseInt(tokens[5]);

            String testTrueStr = testStrings[1];
            String[] tokens_2 = testTrueStr.split(" ");
            int trueResult = Integer.parseInt(tokens_2[9]);

            String testFalseStr = testStrings[2];
            String[] tokens_3 = testFalseStr.split(" ");
            int falseResult = Integer.parseInt(tokens_3[9]);

            this.testOperation = num -> {
                if (num % divisionNum == 0) {
                    return trueResult;
                }
                return falseResult;
            };
        }

        public int performTestOperation(int itemIndex) {
            int worryLevel = this.getMonkeyItemWorryLevel(itemIndex);
            return this.testOperation.apply(worryLevel);
        }
    }

    public static void main(String[] args) {

        try {

            File inputFile = new File("./assets/day11-input.txt");
            Scanner scanner = new Scanner(inputFile);

            // create a monkey group to store all Monkey instances
            ArrayList<Monkey> monkeyGroup = new ArrayList<Monkey>();

            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                String[] tokens = str.split(" ");
                if (tokens[0].equals("Monkey")) {

                    // Initialize a monkey instance
                    int monkeyNumber = 0;
                    if (Character.isDigit(tokens[1].charAt(0))) {
                        monkeyNumber = Character.getNumericValue(tokens[1].charAt(0));
                    }
                    Monkey monkey = new Monkey(monkeyNumber);

                    // populate Starting items
                    String startingItemsStr = scanner.nextLine();
                    startingItemsStr = startingItemsStr.substring(18);
                    String[] itemsList = startingItemsStr.split(", ");
                    for (int i = 0; i < itemsList.length; i++) {
                        int worryLevel = Integer.parseInt(itemsList[i]);
                        monkey.addItemToMonkey(worryLevel);
                    }

                    // set operation for monkey
                    String operationStr = scanner.nextLine();
                    monkey.setOperation(operationStr);

                    // set test Operation for monkey
                    String[] testStrings = new String[3];
                    for (int i = 0; i < testStrings.length; i++) {
                        testStrings[i] = scanner.nextLine();
                    }
                    monkey.setTestOperation(testStrings);

                    monkeyGroup.add(monkey);
                }
            }
            scanner.close();

            runRounds(monkeyGroup);

            long[] twoHighestInspections = getTwoHighestInspections(monkeyGroup);

            long monkeyBusiness = calculateMonkeyBusienss(twoHighestInspections);

            System.out.printf("Result is: %s\n", monkeyBusiness);

        } catch (FileNotFoundException error) {
            System.out.println("An error occurred.");
            error.printStackTrace();
        }
    }

    private static long calculateMonkeyBusienss(long[] twoHighestInspections) {
        return twoHighestInspections[0] * twoHighestInspections[1];
    }

    private static long[] getTwoHighestInspections(ArrayList<Monkey> monkeyGroup) {
        long firstLargest = 0, secondLargest = 0;
        for (int i = 0; i < monkeyGroup.size(); i++) {
            if (monkeyGroup.get(i).getNumberOfInspectionsDone() > firstLargest) {
                secondLargest = firstLargest;
                firstLargest = monkeyGroup.get(i).getNumberOfInspectionsDone();
            } else if (monkeyGroup.get(i).getNumberOfInspectionsDone() > secondLargest) {
                secondLargest = monkeyGroup.get(i).getNumberOfInspectionsDone();
            }
        }
        return new long[] { firstLargest, secondLargest };
    }

    private static void runRounds(ArrayList<Monkey> monkeyGroup) {
        for (int i = 0; i < 20; i++) {

            for (int j = 0; j < monkeyGroup.size(); j++) {
                if (monkeyGroup.get(j).hasItems()) {
                    for (int k = 0; k < monkeyGroup.get(j).getNumberOfItems(); k++) {
                        // perform operation on the item
                        int newWorryLevel = monkeyGroup.get(j).performOperation(k);
                        monkeyGroup.get(j).setMonkeyItemWorryLevel(k, newWorryLevel);

                        // lower worry level of the item
                        monkeyGroup.get(j).lowerItemWorryLevel(k);

                        // perform test operation on item
                        int targetMonkeyNumber = monkeyGroup.get(j).performTestOperation(k);
                        int worryLevelOfItemToThrow = monkeyGroup.get(j).getMonkeyItemWorryLevel(k);

                        // add item to target monkey
                        monkeyGroup.get(targetMonkeyNumber).addItemToMonkey(worryLevelOfItemToThrow);

                        monkeyGroup.get(j).incNumberOfInspectionsDone();
                    }
                    monkeyGroup.get(j).clearItemsList();
                }
            }
        }
    }

    private static boolean isNumeric(String string) {
        try {
            @SuppressWarnings("unused")
            int a = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}