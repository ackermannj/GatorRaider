package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.*;

import java.util.List;

public final class StudentController implements DefenderController
{
	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game,long timeDue)
	{
		int[] actions = new int[Game.NUM_DEFENDER];

		List<Defender> enemies = game.getDefenders();
		Defender defender0 = enemies.get(0);
		Defender defender1 = enemies.get(1);
		Defender defender2 = enemies.get(2);
		Defender defender3 = enemies.get(3);

		List<Integer> possibleDirs0 = defender0.getPossibleDirs();
		List<Integer> possibleDirs1 = defender1.getPossibleDirs();
		List<Integer> possibleDirs2 = defender2.getPossibleDirs();
		List<Integer> possibleDirs3 = defender3.getPossibleDirs();
		Attacker attacker = game.getAttacker();
		Maze maze = game.getCurMaze();

		if (possibleDirs0.size() != 0){
			actions[0] = chase(game,defender0,attacker);
			actions[0] = desperation(defender0,maze,actions[0],attacker);
		}else{
			actions[0] = -1;
		}

		if (possibleDirs1.size() != 0){
			actions[1] = circlePillAttack(game,defender1,maze,attacker);
			actions[1] = desperation(defender1,maze,actions[1],attacker);
		}else{
			actions[1] = -1;
		}

		if (possibleDirs2.size() != 0){
			actions[2] = chase(game,defender2,attacker);
			actions[2] = desperation(defender2,maze,actions[2],attacker);
		}else{
			actions[2] = -1;
		}

		if (possibleDirs3.size() != 0){
			actions[3] = sacrifice(defender3,maze,attacker);
			actions[3] = desperation(defender3,maze,actions[3],attacker);
		}else{
			actions[3] = -1;
		}

		return actions;
	}

	public int chase(Game game,Defender defender,Attacker attacker){
		boolean chase = true;
		if (defender.isVulnerable()){
			chase = false;
		}else{
			chase = true;
		}
		Node pacLocation = attacker.getLocation();
		int nextDirection = defender.getNextDir(pacLocation,chase);
		int action = nextDirection;
		return action;
	}

	public int circlePillAttack(Game game,Defender defender,Maze maze,Attacker attacker){
		List<Node> ppNodes = maze.getPowerPillNodes();
		int action = -1;
		if (defender.getLocation().getPathDistance(attacker.getLocation()) < 60){
			boolean chase = true;
			if (defender.isVulnerable()){
				chase = false;
			}else{
				chase = true;
			}
			action = defender.getNextDir(attacker.getLocation(),chase);
		}else if(game.checkPowerPill(ppNodes.get(0))){
			action = defender.getNextDir(ppNodes.get(0),true);
		}else if(game.checkPowerPill(ppNodes.get(1))){
			action = defender.getNextDir(ppNodes.get(1),true);
		}else if(game.checkPowerPill(ppNodes.get(2))){
			action = defender.getNextDir(ppNodes.get(2),true);
		}else if(game.checkPowerPill(ppNodes.get(3))){
			action = defender.getNextDir(ppNodes.get(3),true);
		}
		return action;
	}

	public int sacrifice (Defender defender, Maze maze, Attacker attacker){
		Node pacLocation = attacker.getLocation();
		int action = -1;
		if (defender.isVulnerable()){
			action = defender.getNextDir(pacLocation,true);
		}else if(defender.getLocation().getPathDistance(attacker.getLocation()) < 60) {
			action = defender.getNextDir(pacLocation, true);
		}else{
			action = defender.getNextDir(maze.getInitialDefendersPosition(),true);
		}
		return action;
	}

	public int desperation(Defender defender, Maze maze, int prevAction,Attacker attacker){
		int action = prevAction;
		List<Node> pillNodes = maze.getPillNodes();
		if (pillNodes.size() < 50){
			Node pillLocation = defender.getTargetNode(pillNodes,true);
			action = defender.getNextDir(pillLocation,true);
			if (defender.getLocation().getPathDistance(attacker.getLocation()) <60) {
				boolean chase;
				if (defender.isVulnerable()) {
					chase = false;
				} else {
					chase = true;
				}
				action = defender.getNextDir(attacker.getLocation(), chase);
			}
		}
		return action;
	}
}