package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RandomBrickGenerator - Generates random Tetris bricks using fair
 * distribution.
 * 
 * Responsibilities:
 * - Randomly selects from 7 standard Tetris pieces (I, J, L, O, S, T, Z)
 * - Maintains queue of upcoming bricks for preview system
 * - Ensures fair distribution via random selection
 * - Pre-generates lookahead bricks for smooth gameplay
 * 
 * Queue Structure:
 * - Position 0: Current active brick
 * - Positions 1-3: Preview bricks for "Next 3" display
 * 
 * @author Umer Imran
 * @version 2.0
 */
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

        // Initialize queue with 4 bricks (1 current + 3 preview)
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
    }

    @Override
    public Brick getBrick() {
        // keep queue stocked with at least 4 bricks (1 current + 3 preview)
        while (nextBricks.size() < 4) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        return nextBricks.poll();
    }

    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /**
     * Get the next 3 bricks that will fall (peek without removing)
     * 
     * @return array of 3 Bricks [next, next+1, next+2]
     */
    public Brick[] getNextThreeBricks() {
        return peekNext(3);
    }

    @Override
    public Brick[] peekNext(int count) {
        // Make sure the internal queue has at least `count` items
        while (nextBricks.size() < count) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }

        Brick[] temp = nextBricks.toArray(new Brick[0]);
        Brick[] result = new Brick[count];

        for (int i = 0; i < count; i++) {
            if (i < temp.length && temp[i] != null) {
                result[i] = temp[i];
            } else {
                Brick b = brickList.get(ThreadLocalRandom.current().nextInt(brickList.size()));
                nextBricks.add(b);
                result[i] = b;
            }
        }

        return result;
    }

}
