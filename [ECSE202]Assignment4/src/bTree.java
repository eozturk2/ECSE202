
/**
 * Implements a B-Tree class using a NON-RECURSIVE algorithm.
 * @author ferrie
 *
 */

public class bTree {
	
	// Instance variables
	
	private bNode root=null;
	private boolean ongoing = true;
	private static final double DELTASIZE = 0.1;

	
	
/**
 * addNode method - adds a new node by descending to the leaf node
 *                  using a while loop in place of recursion.  Ugly,
 *                  yet easy to understand.
 */
	
	
	public void addNode(aBall iBall) {
		
		bNode current;


// Empty tree
		
		if (root == null) {
			root = makeNode(iBall);
		}
		
// If not empty, descend to the leaf node according to
// the input data.  
		
		else {
			current = root;
			while (true) {
				if (iBall.getBallSize() < current.iBall.getBallSize()) {
					
// New data < data at node, branch left
					
					if (current.left == null) {				// leaf node
						current.left = makeNode(iBall); // attach new node here
						break;
					}
					else {									// otherwise
						current = current.left;				// keep traversing
					}
				}
				else {
// New data >= data at node, branch right
					
					if (current.right == null) {			// leaf node	
						current.right = makeNode(iBall);    // attach
						break;
					}
					else {									// otherwise 
						current = current.right;			// keep traversing
					}
				}
			}
		}
		
	}
	
/**
 * makeNode
 * 
 * Creates a single instance of a bNode
 * 
 * @param	int data   Data to be added
 * @return  bNode node Node created
 */
	
	bNode makeNode(aBall iBall) {
		bNode node = new bNode();							// create new object
		node.iBall = iBall;									// initialize data field
		node.left = null;									// set both successors
		node.right = null;									// to null
		return node;										// return handle to new object
	}
	
	
/**
 * inorder method - inorder traversal via call to recursive method
 */
	
	
	
/**
 * traverse_preorder1 method - recursively traverses tree in order (Root-LEFT-RIGHT) and checks if the iBall stored in each node
 * is running, using the isRunning() method of the aBall class. Sets boolean ongoing to true if root.iBall is running. This method is adapted
 * from the preorder traversal method provided in the sample bTree class provided by Prof. Frank P. Perrie.
 * <p>
 * I chose preorder traversal over the other methods because the simulation is running even if only one ball is running. Therefore the method
 * should terminate as soon as it finds a bNode with an aBall that is running.
 */
	
	private void traverse_preorder1(bNode root) {
    	if (root.iBall.isRunning()) ongoing = true;
    	if (root.left != null) traverse_preorder1(root.left);
    	if (root.right != null) traverse_preorder1(root.right);
    }
	/**
	 * Getter method for traverse_preorder1. Boolean ongoing needs to be initially set to false because of the way 
	 * traverse_preorder1 checks the tree.
	 * 
	 * @return running
	 */
	public boolean simIsRunning() {
		ongoing = false;
		traverse_preorder1(root);
		return ongoing;}

	private double last = 0;
	private double x = 0;
	private double y = 0;
	
	/**
	 * ballSort method - Sorts aBall objects by size, implementing the algorithm provided in the Assignment 3 handout.
	 * This is a modified version of the traverse_inorder method provided in the sample bTree class provided 
	 * by Prof. Frank P. Ferrie.
	 * <p>
	 * If STACKTEST is enabled, the method pauses for 1 second after every stack operation, allowing for better testing if needed.
	 * <p>
	 * In Assignment 4, pausing the method with STACKTEST was rendered impractical, so it was removed.
	 * 
	 * @param root: root of the bTree to be sorted
	 */
	private void ballSort(bNode root) {
		if (root.left != null) ballSort(root.left);
		if (root.iBall.getBallSize()-last > DELTASIZE) {
			root.iBall.moveTo(x+1.5*last, 2*root.iBall.getBallSize());
			x += 2*last;
			y = 1.5*root.iBall.getBallSize();
			last = root.iBall.getBallSize();

		}else {
			if(x-last>0) {
			root.iBall.moveTo(x-0.5*last,root.iBall.getBallSize()+2*y);}else{
				root.iBall.moveTo(x,root.iBall.getBallSize()+2*y);	
			}
			y += root.iBall.getBallSize();
			last = root.iBall.getBallSize();

		}
		if (root.right != null) ballSort(root.right);
	}
	
	/**
	 * Resets the positional counters used in ballSort(), allowing for 
	 * multiple stacking operations with a dynamic tree of aBalls.
	 */
	public void stackReset() {
		if (x != 0) {x = 0;}
		if (y != 0) {y = 0;}
		if (last != 0) {last = 0;}
	}
	/**
	 * Getter method for the ballSort(root) method
	 */
	public void doStack() {
		ballSort(root);
	}
	/**
	 * Deletes a tree by simply setting its root to null, and letting the
	 * garbage collector take care of the rest.
	 */
	public void delTree() {root = null;}
	
/**
 * A simple bNode class for use by bTree.  The "payload" has been
 * modified to account for aBall objects.
 * 
 * @author ferrie
 *
 */

class bNode {
	aBall iBall;
	bNode left;
	bNode right;
}
}


