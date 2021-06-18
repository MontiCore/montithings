package examples.cocoTest.valid;

component Sum {
	port in int first;
	port in int second;
	sync first, second;

	port out int result;

    int temp = 0;
    behavior {
        temp = first + second;
        log("Sum: " + temp);
        result = temp;
    }
}
