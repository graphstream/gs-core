graph [
	Id "Dynamic"

	step 1
	
	node [ Id "A" ]
	node [ Id "B" ]
	node [ Id "C" ]
	
	step 2
	
	edge [ Id "AB" Source "A" Target "B" ]
	edge [ Id "BC" Source "B" Target "C" ]
	edge [ Id "CA" Source "C" Target "A" ]
	
	step 3
	
	+node [ Id "B" graphics [
		fill "red"	
	]]
	
	step 4
	
	-node [ Id "A" ]
	
	step 5
	
	+node [ Id "B" Label "foo" ]
	
	step 6
	
	-edge "BC"
	
	step 7
	
	node "Z"
	
	step 8
	
	+node [ Id "B" -Label [] ]
	
	step 9
	
	directed true
	
	edge [ Id "ZB" Source "Z" Target "B" ]
	edge [ Id "ZC" Source "Z" Target "C" ]
	edge [ Id "BC" Source "B" Target "C" ]
]