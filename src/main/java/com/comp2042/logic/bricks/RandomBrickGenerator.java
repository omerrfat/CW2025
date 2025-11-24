package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 1) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /**
     * Get the next 3 bricks that will fall
     * 
     * @return array of 3 Bricks [next, next+1, next+2]
     */
    public Brick[] getNextThreeBricks() {
        Brick[] result = new Brick[3];
        Brick[] temp = nextBricks.toArray(new Brick[0]);

        for (int i = 0; i < 3; i++) {
            if (i < temp.length) {
                result[i] = temp[i];
            } else {
                // Generate more bricks if needed
                Brick newBrick = brickList.get(ThreadLocalRandom.current().nextInt(brickList.size()));
                nextBricks.add(newBrick);
                result[i] = newBrick;
            }
        }

        return result;
    }
}
