package linked_list

type Node struct {
	Value interface{}
	next  *Node
}

func newNode(val interface{}) *Node {
	return &Node{val, nil}
}

func (node *Node) Next() *Node {
	return node.next
}
