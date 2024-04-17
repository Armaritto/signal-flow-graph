package org.example.backend.Services;

import java.util.*;

public class SignalFlowGraph {
    private HashMap<Integer, Double> loopsGain = new HashMap<>();
    private final List<Node> nodes;
    public SignalFlowGraph() {
        nodes = new ArrayList<>();
    }

    public void addNode(String name) {
        nodes.add(new Node(name));
    }

    public void addEdge(String sourceName, String destName, double gain) {
        Node source = getNode(sourceName);
        Node dest = getNode(destName);
        if (source != null && dest != null) {
            source.addEdge(dest, gain);
        }
    }

    private Node getNode(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    public String masonsGainFormula(String sourceName, String destName) {
        List<List<Node>> allPaths = findAllPaths(sourceName, destName);
        List<Double> forwardPathsGain = new ArrayList<>();
        List<List<Node>> allLoops = findAllLoops();
        List<Double> allLoopsGain = new ArrayList<>();
        List<List<Integer>> nonTouchingLoops = findNonTouchingLoops();
        List<Double> nonTouchingLoopsGain = new ArrayList<>();
        // Calculate total forward path gain
        for (List<Node> path : allPaths) {
            double forwardPathGain = computeForwardPathGain(path);
            forwardPathsGain.add(forwardPathGain);
        }

        for(List<Node> loop : allLoops){
            double loopGain = computeAllLoopsGain(loop);
            allLoopsGain.add(loopGain);
        }

        // Print all lists
        StringBuilder forwardPathString = new StringBuilder();
        forwardPathString.append("All Paths:").append("\n");
        int i = 0;
        for (List<Node> path : allPaths) {
            forwardPathString.append(pathToString(path));
            forwardPathString.append(", Gain = ").append(forwardPathsGain.get(i));
            i++;
            forwardPathString.append("\n");
        }
        System.out.println(forwardPathString);

        // Print all loops
        StringBuilder allLoopsString = new StringBuilder();
        allLoopsString.append("All Loops:").append("\n");
        i=0;
        for(List<Node> loop : allLoops){
            loopsGain.put(i, allLoopsGain.get(i));
            allLoopsString.append("Loop ").append(i + 1).append(": ");
            allLoopsString.append(loopToString(loop));
            allLoopsString.append(", Gain = ").append(allLoopsGain.get(i));
            i++;
            allLoopsString.append("\n");
        }
        System.out.println(allLoopsString);

        //Print all non-touching loops
        StringBuilder nonTouchingLoopString = new StringBuilder();
        nonTouchingLoopString.append("All Non-Touching Loops:").append("\n");
        for(List<Integer> nonTouchingLoop : nonTouchingLoops){
            for(Integer loopIndex : nonTouchingLoop){
                nonTouchingLoopString.append("Loop ").append(loopIndex + 1);
                if(!loopIndex.equals(nonTouchingLoop.get(nonTouchingLoop.size() - 1))){
                    nonTouchingLoopString.append(" and ");
                }
                else
                    nonTouchingLoopString.append(" :");
            }
            //gain of non-touching loops
            nonTouchingLoopString.append(" Gain = ");
            double gain = 1.0;
            for(Integer loopIndex : nonTouchingLoop){
                gain *= allLoopsGain.get(loopIndex);
            }
            nonTouchingLoopString.append(gain);
            nonTouchingLoopString.append("\n");
        }
        System.out.println(nonTouchingLoopString);

        //Calculate delta
        double delta = getDelta(nonTouchingLoops);
        StringBuilder deltaString = new StringBuilder();
        deltaString.append("Delta = ").append(delta);
        System.out.println(deltaString);

        // Calculate the Delta i for all paths
        i=0;
        List<Double> deltaIList = new ArrayList<>();
        StringBuilder deltaIString = new StringBuilder();
        for (List<Node> path : allPaths) {
            i++;
            double deltaI = getDeltaI(nonTouchingLoops, path, allLoops);
            deltaIString.append("Delta " + i + " = " + deltaI + "\n");
            deltaIList.add(deltaI);
        }
        System.out.println(deltaIString);
        // Calculate TF
        double TF=0;
        for(int j=0;j<forwardPathsGain.size();j++){
            TF += forwardPathsGain.get(j) * deltaIList.get(j);
        }
        TF /= delta;
        StringBuilder TFString = new StringBuilder();
        TFString.append("Transfer Function = ").append(TF);
        StringBuilder result = new StringBuilder();
        result.append(forwardPathString).append("\n").append(allLoopsString).append("\n").append(nonTouchingLoopString).append("\n").append(deltaString).append("\n").append(deltaIString).append("\n").append(TFString);
        return result.toString();
    }

    private String pathToString(List<Node> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName());
            if (i < path.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    private String loopToString(List<Node> loop){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<loop.size();i++){
            sb.append(loop.get(i).getName());
            if(i<loop.size()-1){
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    private double computeForwardPathGain(List<Node> path) {
        double gain = 1.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node source = path.get(i);
            Node dest = path.get(i + 1);
            Edge edge = getEdge(source, dest);
            if (edge != null) {
                System.out.println(edge.getGain());
                gain *= edge.getGain();
            } else {
                return 0.0;
            }
        }
        System.out.println();
        return gain;
    }

    private double computeAllLoopsGain(List<Node> loop){
        double gain = 1.0;
        for (int i = 0; i < loop.size() - 1; i++) {
            Node source = loop.get(i);
            Node dest = loop.get(i + 1);
            Edge edge = getEdge(source, dest);
            if (edge != null) {
                gain *= edge.getGain();
            } else {
                return 0.0;
            }
        }
        return gain;
    }

    private Edge getEdge(Node source, Node destination) {
        for (Edge edge : source.getEdges()) {
            if (edge.getDestination() == destination) {
                return edge;
            }
        }
        return null;
    }

    private List<List<Node>> findAllPaths(String sourceName, String destName) {
        Node source = getNode(sourceName);
        Node dest = getNode(destName);
        if (source == null || dest == null) {
            return new ArrayList<>();
        }

        List<List<Node>> allPaths = new ArrayList<>();
        List<Node> currentPath = new ArrayList<>();
        currentPath.add(source);
        findPaths(source, dest, currentPath, allPaths);
        return allPaths;
    }

    private void findPaths(Node current, Node dest, List<Node> currentPath, List<List<Node>> allPaths) {
        if (current == dest) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }
        for (Edge edge : current.getEdges()) {
            if (!currentPath.contains(edge.getDestination())) {
                currentPath.add(edge.getDestination());
                findPaths(edge.getDestination(), dest, currentPath, allPaths);
                currentPath.remove(currentPath.size() - 1);
            }
        }
    }

    private List<List<Node>> findAllLoops() {
        List<List<Node>> allLoops = new ArrayList<>();
        for (Node node : nodes) {
            Stack<Node> stack = new Stack<>();
            stack.push(node);
            findLoops(node, node, stack, allLoops);
        }
        for(List<Node> loop : allLoops){
            loop.add(loop.get(0));
        }
        return allLoops;
    }

    private void findLoops(Node startNode, Node currentNode, Stack<Node> stack, List<List<Node>> allLoops) {
        for (Edge edge : currentNode.getEdges()) {
            Node nextNode = edge.getDestination();
            if (nextNode == startNode && !stack.isEmpty() && stack.firstElement().equals(startNode)) {
                List<Node> loop = new ArrayList<>(stack);
                if (!containsLoop(allLoops, loop)) {
                    allLoops.add(loop);
                }
            } else if (!stack.contains(nextNode)) {
                stack.push(nextNode);
                findLoops(startNode, nextNode, stack, allLoops);
                stack.pop();
            }
        }
    }

    private boolean containsLoop(List<List<Node>> allLoops, List<Node> loop) {
        for (List<Node> existingLoop : allLoops) {
            if (new HashSet<>(existingLoop).equals(new HashSet<>(loop))) {
                return true;
            }
        }
        return false;
    }
    public List<List<Integer>> findNonTouchingLoops() {
        List<List<Node>> allLoops = findAllLoops();
        List<List<Integer>> nonTouchingLoops = new ArrayList<>();
        for (int i = 2; i <= allLoops.size(); i++) {
            findNonTouchingLoopsHelper(allLoops, new ArrayList<>(), nonTouchingLoops, 0, i);
        }
        return nonTouchingLoops;
    }

    private void findNonTouchingLoopsHelper(List<List<Node>> allLoops, List<Integer> current, List<List<Integer>> result, int start, int n) {
        if (n == 0) {
            if (isNonTouching(allLoops, current)) {
                result.add(new ArrayList<>(current));
            }
            return;
        }
        for (int i = start; i <= allLoops.size() - n; i++) {
            current.add(i);
            findNonTouchingLoopsHelper(allLoops, current, result, i + 1, n - 1);
            current.remove(current.size() - 1);
        }
    }

    private boolean isNonTouching(List<List<Node>> allLoops, List<Integer> indices) {
        for (int i = 0; i < indices.size(); i++) {
            for (int j = i + 1; j < indices.size(); j++) {
                if (isTouching(allLoops.get(indices.get(i)), allLoops.get(indices.get(j)))) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean isTouching(List<Node> loop1, List<Node> loop2) {
        for (Node node : loop1) {
            if (loop2.contains(node)) {
                return true;
            }
        }
        return false;
    }
    public Double getDelta(List<List<Integer>> nonTouchingLoops){
        double delta = 1.0;
        for(int i=0;loopsGain.containsKey(i);i++){
            delta -= loopsGain.get(i);
        }
        for(List<Integer> nonTouchingLoop : nonTouchingLoops){
            delta += loopsGain.get(nonTouchingLoop.get(0)) * loopsGain.get(nonTouchingLoop.get(1));
        }
        return delta;
    }
    public Double getDeltaI(List<List<Integer>> nonTouchingLoops, List<Node> path, List<List<Node>> allLoops){
        double delta = 1.0;
        for(int i=0;loopsGain.containsKey(i);i++){
            if(!isTouching(path, allLoops.get(i)))
                delta -= loopsGain.get(i);
        }
        for(List<Integer> nonTouchingLoop : nonTouchingLoops){
            boolean isTouching = false;
            double product = 1.0;
            for(Integer loopIndex : nonTouchingLoop){
                if(isTouching(path, allLoops.get(loopIndex))){
                    isTouching = true;
                    break;
                }
                product *= loopsGain.get(loopIndex);
            }
            if(!isTouching)
                delta += product;
        }
        return delta;
    }
}
