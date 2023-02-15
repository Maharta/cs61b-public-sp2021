
public class LargerThanFourNeighbors {
    public static Dog[] largerThanFourNeighbors(Dog[] dogs) {
        Dog[] result = new Dog[dogs.length];
        int count = 0;
        for (int i = 0; i < dogs.length; i++) {
            boolean isBiggerThanLeft = true;
            boolean isBiggerThanRight = true;
            if (hasLeftNeighbor(i)) {
                isBiggerThanLeft = isBiggest(dogs[i], dogs[i - 1], dogs[i - 2]);
            }
            if (hasRightNeighbor(i, dogs.length)) {
                isBiggerThanRight = isBiggest(dogs[i], dogs[i + 1], dogs[i + 2]);
            }
            /* Add the dog object to the result array if it is bigger than its neighbours */
            if(isBiggerThanLeft && isBiggerThanRight) {
                result[count] = dogs[i];
                count++;
            }
        }
        return result;
    }

    public static boolean hasLeftNeighbor(int i) {
        return i >= 2;
    }
    public static boolean hasRightNeighbor(int i, int arrLength) {
        return i <= arrLength - 1 - 2;
    }
    public static boolean isBiggest(Dog dog, Dog dog1, Dog dog2) {
        return dog.weightInPounds > dog1.weightInPounds && dog.weightInPounds > dog2.weightInPounds;
    }
    public static void main(String[] args) {
        Dog[] dogs = new Dog[]{
                new Dog(10),
                new Dog(15),
                new Dog(20),
                new Dog(15),
                new Dog(10),
                new Dog(5),
                new Dog(10),
                new Dog(15),
                new Dog(22),
                new Dog(15),
                new Dog(20),
        };

        Dog[] biggestDogs = largerThanFourNeighbors(dogs);
        for (Dog dog : biggestDogs)   {
            System.out.println(dog.weightInPounds);
        }

    }
}
