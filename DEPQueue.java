package assignment08;

/*
 * 
 * This is a double ended queue for min and max values based on lexicographical order. Everything starts with nothing, in order to begin
 * you have to add each of the values. The trees for this DEPQueue are stored in two arrays in order to get the correct complexity.
 * 
 * @author Ashton Schmidt
 * @version November 29, 2017 
 */
public class DEPQueue <T>
{
	// variables and heaps
	private int comparasonCount;
	private int swapCount;
	private int sizeOfHeap;
	private JustData [] minHeap;
	private JustData [] maxHeap;

	//constructor
	DEPQueue()
	{
		comparasonCount = 0;
		swapCount = 0;
		sizeOfHeap = 0; //1 greater than the number of elments in the arrays.
		minHeap = new JustData[10];
		maxHeap = new JustData[10];
	}
	
	/**
	 * Inserts a string into both the min and max heaps. It makes soure that there is enough space to add,
	 * and ensures that the heap will retain its heap definition through the bubble up function.
	 * 
	 * @param data
	 */
	public void insert(String data)
	{
		JustData dataToAdd = new JustData(data); 
		int indexToBeAdded = sizeOfHeap;

		//check for enough space
		boolean enough = this.enoughSpace(indexToBeAdded);
		if(!enough)
			this.makeMoreSpace();
		
		//insert to min and bubble up if index is not 0.
		//swap done here and not in method due to possible index out out of bounds exceptions.
		minHeap[indexToBeAdded] = dataToAdd;
		dataToAdd.posInMinHeap = indexToBeAdded;
		if(indexToBeAdded != 0)
			this.bubbleUpMin(minHeap[indexToBeAdded]);
		
		//swap the top to ensure that the highest priorty is at the top if index is not 0.
		//swap done here and not in method due to possible index out out of bounds exceptions.
		maxHeap[indexToBeAdded] = dataToAdd;
		maxHeap[indexToBeAdded].posInMaxHeap = indexToBeAdded;
		if(indexToBeAdded != 0)
			this.bubbleUpMax(maxHeap[indexToBeAdded]);
		sizeOfHeap ++;
	}
	/**
	 * Removes the max value from both the min and max heap.
	 * @return the value removed.
	 */
	public String removeMin()
	{
		JustData returnValue = minHeap[0];
		if(returnValue == null)
			return null;
		//swap the root with the lowest value. (numIndexInHeapFilled is empty so we want to step back one!)
		//swap done here and not in method due to possible index out out of bounds exceptions.
		minHeap[0] = minHeap[sizeOfHeap - 1];
		minHeap[0].posInMinHeap = 0;
		minHeap[sizeOfHeap -1] = null;
		swapCount++;
		sizeOfHeap --;
	
			if(!(sizeOfHeap <= 0))
				bubbleDownMin(minHeap[0]);
		removeFromMaxHeap(returnValue.posInMaxHeap);
	
		return returnValue.data;
	}
	/**
	 * Removes the max value from both the min and max heap.
	 * @return the value removed.
	 */
	public String removeMax()
	{
		JustData returnValue = maxHeap[0];
		if(returnValue == null)
			return null;
		//swap done here and not in method due to possible index out out of bounds exceptions.
		maxHeap[0] = maxHeap[sizeOfHeap -1];
		maxHeap[0].posInMaxHeap = 0; // make new position.
		maxHeap[sizeOfHeap -1] = null;
		
		swapCount++;
		sizeOfHeap --;
		
		if(!(sizeOfHeap <= 0))
			bubbleDownMax(maxHeap[0]);
		removeFromMinHeap(returnValue.posInMinHeap);
		
		
		return returnValue.data;
	}
	
	//*********************************** Private helper functions for the heaps (bubble up/down, removefrom(min/max).

	/**
	 * compare the current(child) to the parent to see if it needs to swap with the parent and
	 * go up the minHeap.
	 * @param current
	 */
	private void bubbleUpMin(JustData current) //passing in the JustData object to accss the properties faster.
	{
		int index = current.posInMinHeap;
		int parentIndex = this.getParent(current.posInMinHeap);
		int result = minHeap[parentIndex].data.compareTo(minHeap[index].data);
		comparasonCount++;
		
		if(result > 0) //parent is bigger
		{
			swap(index, parentIndex, minHeap);			
			bubbleUpMin( minHeap[parentIndex]);

		}
	}
	/**
	 * compare the current(child) to the parent to see if it needs to swap with the parent and
	 * go up the maxHeap.
	 * @param current
	 */
	private void bubbleUpMax( JustData current)
	{
		int index = current.posInMaxHeap;
		int parentIndex = this.getParent(index);
		int result = maxHeap[parentIndex].data.compareTo(maxHeap[index].data);
		comparasonCount++;
		
		if(result < 0)
		{	//swap
			swap(index, parentIndex, maxHeap);
			bubbleUpMax(maxHeap[parentIndex]);
		}
	}
	
	/**
	 * Moves lower priority (larger) items down in the heap.
	 * 
	 * How it works:
	 * Takes in a JustData object and collects is children, if there are actually children compare them,
	 * if not then we see if either of the children are there. If not then we give it to the left child.
	 * We then compare the child to the parent and see if we need to swap, if we do we call the swap method
	 * and recursivly call ourselves again.
	 * @param current
	 */
	private void bubbleDownMin( JustData current)
	{
		//variables
		int indexCurrent = current.posInMinHeap;
		JustData leftChild = null;
		JustData rightChild = null;
		int resultOfChildDuel = 0;
		
		//gather and check child data.
		int leftChildIndex = this.getLeftChildIndex(indexCurrent);
		int rightChildIndex = this.getRightChildIndex(indexCurrent);
		if(leftChildIndex != -1)
			 leftChild = minHeap[leftChildIndex];
		if(rightChildIndex != -1)
			rightChild = minHeap[rightChildIndex];
		
		//compare children if possible.
		if(leftChild != null && rightChild != null)
		{
			resultOfChildDuel = leftChild.data.compareTo(rightChild.data);
			comparasonCount ++;
		}
		else if(leftChild!= null)
			resultOfChildDuel = -1;
		else if(rightChild!= null)
			resultOfChildDuel = 1;
		
		if (resultOfChildDuel < 1) //left is smaller of the children
		{
			if(leftChild == null)
			{
				return; //falesafe because if left is null at all and cannot be compared childDuel is set to zero going in here.
			}
			if(leftChild.data.compareTo(current.data) < 0) //left is smaller than the parent, swap
			{
				comparasonCount ++;
				swap(indexCurrent, leftChildIndex, minHeap);
				bubbleDownMin(minHeap[leftChildIndex]);
			}
		}
		else //right is smaller of the children
		{
			if(rightChild.data.compareTo(current.data) < 0) //right is smaller than the parent, swap
			{
				comparasonCount++;
				swap(indexCurrent, rightChildIndex, minHeap);
				bubbleDownMin(minHeap[rightChildIndex]);
			}
		}
	}
	
	/**
	 * Moves lower priority items (smaller) down in the heap.
	 * 
	 * How it works:
	 * Takes in a JustData object and collects is children, if there are actually children compare them,
	 * if not then we see if either of the children are there. If not then we give it to the left child.
	 * We then compare the child to the parent and see if we need to swap, if we do we call the swap method
	 * and recursivly call ourselves again.
	 * @param current
	 */
	private void bubbleDownMax( JustData current)
	{
		//variables
		int indexCurrent = current.posInMaxHeap;
		JustData leftChild = null;
		JustData rightChild = null;
		int resultOfChildDuel = 0;
		//gather and check child data
		int leftChildIndex = this.getLeftChildIndex(indexCurrent);
		int rightChildIndex = this.getRightChildIndex(indexCurrent);
		if(leftChildIndex != -1)
			 leftChild = maxHeap[leftChildIndex];
		if(rightChildIndex!= -1)
			rightChild = maxHeap[rightChildIndex];
		
		//compare children if possible
		if(leftChild != null && rightChild != null)
		{
			resultOfChildDuel = leftChild.data.compareTo(rightChild.data);
			comparasonCount ++;
		}
		else if(leftChild!= null)
			resultOfChildDuel = 1;
		else if(rightChild!= null)
			resultOfChildDuel = -1;
		
		if (resultOfChildDuel >= 0) //left is larget of the children
		{
			if(leftChild == null)
			{
				return; //falesafe because if left is null at all and cannot be compared childDuel is set to zero going in here.
			}
			
			if(leftChild.data.compareTo(current.data) > 0) //left is larger than the parent, swap
			{
				comparasonCount++;
				swap(indexCurrent, leftChildIndex, maxHeap);
				bubbleDownMax(maxHeap[leftChildIndex]);
			}
		}
		else //right is smaller of the children
		{
			if(rightChild.data.compareTo(current.data) > 0) //right is larger than the parent, swap
			{
				comparasonCount++;
				swap(indexCurrent, rightChildIndex, maxHeap);
				bubbleDownMax(maxHeap[rightChildIndex]);
			}
		}
	}

	/**
	 * removes data from the min heap, by swaping the data at the target index with the last and bubbling up the heap.
	 * increases swaps.
	 * @param target
	 */
	private void removeFromMinHeap(int target)
	{ 
		//swap target with the end remove from end and make the heap smaller.
		swap(target, sizeOfHeap, minHeap);
		minHeap[sizeOfHeap] = null;	//decremented in previous call so no -1	
		if(target!= sizeOfHeap)
			 this.bubbleUpMin(minHeap[target]);
	}
	/**
	 * removes data from the max heap, by swaping the data at the target index with the last and bubbling up the heap.
	 * increases swaps.
	 * @param target
	 */
	private void removeFromMaxHeap(int target)
	{ 
		swap(target, sizeOfHeap, maxHeap);
		maxHeap[sizeOfHeap] = null; //out of the range of the heap so we dont have to decrement, we did in the previous call.
		if(target != sizeOfHeap)
			this.bubbleUpMax(maxHeap[target]);
	}
	
	
	//************************ Private helper funtions, just to help with the functionality of the code not direcltly related to the heaps.
	/**
	 * takes in two indexes and a heap, and swaps the values also increasing the number of swaps in total.
	 * @param index1
	 * @param index2
	 * @param heap
	 */
	private void swap(int index1, int index2 , JustData[] heap)
	{
		String typeOfHeap = "maxHeap";
		if(heap == minHeap)
			typeOfHeap = "minHeap";
		JustData temp = heap[index1];
		heap[index1] = heap[index2];
		heap[index2] = temp;
		if(typeOfHeap.equals("minHeap"))
		{
			heap[index1].posInMinHeap = index1;
			heap[index2].posInMinHeap = index2;
		}
		else
		{
			heap[index1].posInMaxHeap = index1;
			heap[index2].posInMaxHeap = index2;
		}
		swapCount++;
	}
	
	/**
	 * takes in a parent and returns its right child if there is a right child and in in the bounds of the array, otherwise
	 * it returns -1.
	 * @param currentPos
	 * @return index of right child, or -1
	 */
	private int getRightChildIndex(int currentPos)
	{
		int indexOfRightChild = (currentPos *2) + 2;
		if(indexOfRightChild >= sizeOfHeap)
			indexOfRightChild = -1;
		return indexOfRightChild;
	}
	/**
	 * takes in a parent and returns its left child if there is a right child and in in the bounds of the array, otherwise
	 * it returns -1.
	 * @param currentPos
	 * @return index of right child, or -1
	 */
	private int getLeftChildIndex(int currentPos)
	{
		int indexOfLeftChild = (currentPos *2) + 1;
		if(indexOfLeftChild >= sizeOfHeap)
			indexOfLeftChild = -1;
		return indexOfLeftChild;
	} 
	/**
	 * takes in a child index and returns out the parent index.
	 * @param childIndex
	 * @return index of parent
	 */
	private int getParent(int childIndex)
	{
		 int index = (int)Math.floor((childIndex - 1)  / 2);

	     if(index < 0)
	     {
	       return 0;
	     }
	     
		return index;
	}
	/**
	 * the number of indexes that have been filled in the heap not the number of spaces that are left in the heap
	 * @return
	 */
	public int size()
	{
		return sizeOfHeap;
	}
	/**
	 * 
	 * @return comparason count for heaps.
	 */
	public long getComparisonCount()
	{
		return comparasonCount;
	}
	/**
	 * 
	 * @return swap count for the heaps
	 */
	public long getSwapCount()
	{
		return swapCount;
	}
	/**
	 * checks if there is enough space in the heap by checking how many indexes have already
	 * been filled and making sure that is less than the array length, and making sure that the index 
	 * is within the array.
	 * @param index
	 * @return
	 */
	private boolean enoughSpace(int index)
	{
		return sizeOfHeap < minHeap.length && index < minHeap.length;
	}
	
	/**
	 * makes more space in the min heap and max heap because they contain the same values.
	 */
	private void makeMoreSpace()
	{
		int newLength = minHeap.length * 2;
		JustData [] tempMinHeap = new JustData[newLength];
		JustData [] tempMaxHeap = new JustData[newLength];
		//copy values into temp
		for(int index = 0; index < minHeap.length; index ++)
		{
			tempMinHeap[index] = minHeap[index];
			tempMaxHeap[index] = maxHeap[index];
		}
		//set equal.
		minHeap = tempMinHeap;
		maxHeap = tempMaxHeap;
	}
	
	//********************************* Static inner class to create objects to put in the heap!
	
	/*
	 * internal static class used to keep track of the index in both the min and max heap for the data
	 * that is inserted. This makes it easy to know where the data is in both of the heaps.
	 */
	static class JustData
	{
		String data;
		int posInMinHeap;
		int posInMaxHeap;
		JustData(String data)
		{
			this.data = data;
			posInMinHeap = -1;
			posInMaxHeap = -1;
		}
	}
	
	
	

}
