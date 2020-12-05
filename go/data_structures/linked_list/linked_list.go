package linked_list

type LinkedList struct {
	size int
	head *Node
	tail *Node
}

func New() *LinkedList {
	return &LinkedList{0, nil, nil}
}

func (this *LinkedList) Size() int {
	return this.size
}

func (this *LinkedList) Add(val interface{}) {
	if this.size == 0 {
		this.tail = newNode(val)
		this.head = this.tail
	} else {
		this.tail.next = newNode(val)
		this.tail = this.tail.next
	}

	this.size++
}

func (this *LinkedList) Front() *Node {
	return this.head
}
