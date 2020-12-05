// go run data_structures/linked_list/main/main.go

package main

import (
	"fmt"
	linkedlist "prez/gocode/data_structures/linked_list"
)

func main() {
	myList := linkedlist.New()
	for i := 0; i < 10; i++ {
		myList.Add(i)
	}

	for node := myList.Front(); node != nil; node = node.Next() {
		fmt.Println(node.Value)
	}
	fmt.Printf("The list contains %d elements.\n", myList.Size())
}
