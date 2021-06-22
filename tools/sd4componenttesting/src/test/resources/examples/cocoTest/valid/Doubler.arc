package examples.cocoTest.valid;

component Doubler {
	port in int input;
	port out int result;

	Sum sum;

	input->sum.first;
	input->sum.second;
	sum.result->result;
}
