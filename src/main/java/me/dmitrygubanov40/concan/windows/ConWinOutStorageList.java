package me.dmitrygubanov40.concan.windows;


import java.util.ArrayList;
import java.util.List;



/**
 * Special list for rapid operation with first and last elements.
 * Just some sort of intellectual type of 'ArrayList'.
 * Keep the index of 'first' element, and use 'null' for removing
 * elements at the beginning of list.
 * @author Dmitry Gubanov, dmitry.gubanov40@gmail.com
 */
class ConWinOutStorageList<T>
{
    
    private static final int DELETED_ELEMENTS_LIMIT;
    
    static {
        DELETED_ELEMENTS_LIMIT = 1000;
    }
    
    
    ////////////////////////////
    
    
    // if we need dead-end ArrayList - should start from this element
    private int firstElementIndex;
    
    // Real container of storage list.
    private ArrayList<T> list;
    
    
    ////////////////////////////
    
    
    /**
     * Full constructor limit.
     * @param initSize limit for array list initialization
     */
    public ConWinOutStorageList(final int initSize) {
        this.firstElementIndex = 0;
        this.list = new ArrayList<>(initSize);
    }
    
    /**
     * Default empty constructor.
     */
    public ConWinOutStorageList() {
        this.firstElementIndex = 0;
        this.list = new ArrayList<>();
    }
    
    
    ////////////////////////////
    
    
    /**
     * When 'virtualIndex' is called, in terms of ArrayList
     * we would address to this index.
     * @return index for regular ArrayList
     */
    private int getArrayListIndex(final int virtualIndex) {
        return (virtualIndex + this.firstElementIndex);
    }
    
    
    
    /**
     * Refresh real inner ArrayList if necessary.
     * I.e. get rid of deleted 'null' elements.
     */
    private void checkDeletedElements() {
        if ( this.firstElementIndex < ConWinOutStorageList.DELETED_ELEMENTS_LIMIT ) {
            // do nothing before too much elements are deleted
            return;
        }
        //
        // inner list is re-esteblished:
        List<T> reesteblishedList = this.list.subList(this.firstElementIndex, this.list.size());
        this.list = new ArrayList<>(reesteblishedList);
        this.firstElementIndex = 0;
    }
    
    
    
    /**
     * Calculate the number of non-deleted elements.
     * @return size in terms of ArrayList
     */
    public int size() {
        final int sizeResult = this.list.size() - this.firstElementIndex;
        //
        return sizeResult;
    }
    
    
    
    /**
     * Get simple list without 'deleted' elements.
     * @return 
     */
    public ArrayList<T> getArrayList() {
        List<T> regularList = this.list.subList(this.firstElementIndex, this.list.size());
        ArrayList<T> resultList = new ArrayList<>(regularList);
        //
        return resultList;
    }
    
    
    
    /**
     * Built-in rapid deleter of the first element in the list.
     * It will be marked as 'null', and the virtual index will be moved.
     * @param numberOfElements how many first elements of list will be mark as 'deleted'
     * @throws ArrayIndexOutOfBoundsException when want to delete element from empty list
     */
    public T removeFirst() throws ArrayIndexOutOfBoundsException {
        if ( this.size() <= 0 ) {
            String excMsg = "Cannot delete element: the list is empty";
            throw new ArrayIndexOutOfBoundsException(excMsg);
        }
        //
        // element is erased (marked as 'null'):
        final int indexForArrayList = this.getArrayListIndex(0);
        T deletedElement = this.list.get(indexForArrayList);
        this.list.set(indexForArrayList, null);
        //
        // element is 'removed', now it is hidden:
        this.firstElementIndex++;
        //
        // in case of too many removed elements - do extra measures
        this.checkDeletedElements();
        //
        return deletedElement;
    }
    
    
    
    /**
     * Special ArrayList 'add'-cover, index only.
     * Important: can be used only via index.
     * Works in special way with zero-index (first element),
     * or as regular ArrayList 'remove'-cover in other cases.
     * @param virtualIndex index of element to remove
     * @return ArrayList remove result (copy of element which was removed)
     */
    public T remove(final int virtualIndex) {
        if ( 0 == virtualIndex ) {
            // when it was requested to remove the first element only
            return this.removeFirst();
        }
        //
        final int indexForArrayList = this.getArrayListIndex(virtualIndex);
        //
        return this.list.remove(indexForArrayList);
    }
    
    /**
     * Safety alert.
     * Class doe not support list work via objects.
     */
    public T remove(Object o) throws UnsupportedOperationException {
        String alert = "List method does not support object operations";
        throw new UnsupportedOperationException(alert);
    }
    
    
    
    /**
     * Regular ArrayList 'add'-cover.
     * @param toAdd element to be added into the inner list
     * @return ArrayList add result (was added or not)
     */
    public boolean add(T toAdd) {
        return this.list.add(toAdd);
    }
    
    /**
     * Regular ArrayList 'get'-cover.
     * We must request 'virtual' index,
     * like 'removed' elements were really removed from the list.
     * @param virtualIndex index of element to get
     * @return ArrayList get result (list element)
     */
    public T get(final int virtualIndex) {
        final int indexForArrayList = this.getArrayListIndex(virtualIndex);
        //
        return this.list.get(indexForArrayList);
    }
    
    /**
     * Regular ArrayList 'set'-cover.
     * @param virtualIndex index of element to set
     * @param element new element in list
     * @return ArrayList set result
     */
    public T set(final int virtualIndex, T element) {
        final int indexForArrayList = this.getArrayListIndex(virtualIndex);
        //
        return this.list.set(indexForArrayList, element);
    }
    
    
    
}
