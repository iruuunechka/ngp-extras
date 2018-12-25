package ru.ifmo.ctd.ngp.demo.paramchooser.controller.agents.utree;

/**
 * @author Arkadii Rost
 */
public class TreeNode<T> {
    private static final int UNDEFINED = -1;
    protected TreeNode<T> left;
    protected TreeNode<T> right;

    private T value;
    private int decideIndex;
    private double splitValue;

    public TreeNode(T value) {
        this.value = value;
        decideIndex = UNDEFINED;
        splitValue = Double.NaN;
    }

    public final TreeNode<T> findLeaf(double... params) {
        if (isLeaf())
            return this;
        TreeNode<T> nextNode = params[decideIndex] < splitValue ? left : right;
        return nextNode.findLeaf(params);
    }

    public void split(int decideIndex, double splitValue,
                      TreeNode<T> left, TreeNode<T> right)
    {
        value = null;
        this.decideIndex = decideIndex;
        this.splitValue = splitValue;
        this.left = left;
        this.right = right;
    }

    public T getValue() {
        return value;
    }

    public boolean isLeaf() {
        return value != null;
    }
}
