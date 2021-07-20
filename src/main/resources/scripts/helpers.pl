% helpers
merge_list([],L,L).
merge_list([H|T],L,[H|M]):-
    merge_list(T,L,M).

% Adapted from https://stackoverflow.com/questions/4578755/permuted-combinations-of-the-elements-of-a-list-prolog
% returns all possible combinations of a given list which length is less than equal a specific number
list_combinations_of_length_lte(_,0,[]).

list_combinations_of_length_lte(Input,N,Output) :-
   findall(Num, between(1, N, Num), List_of_length_N),
   same_length(List_of_length_N,Full),
   Output = [_|_],
   prefix(Output,Full),
   foldl(select,Output,Input,_).



% get list of all devices that are online
list_online_devices(L) :-
    include(property("state","online"),_,L).

% filter list on property
filter_list(Input, Key, Value, Output) :-
    include(property(Key, Value), Input, Output).

list_online_property(Key, Value, Output) :-
    list_online_devices(L1),
    findall(X,property(Key, Value, X), L2),
    intersection(L1, L2, Output).

% whereas the method is implemented as a helper function
% e.g. ComponentDistributionA = Droom_temp_controller_latest
%      ComponentDistributionB = Droom_temp_sensor_latest
% = [ [raspy1,raspy2,raspy3], [raspy2,raspy4,raspy5]]
% hence, the intersection of those two lists should be empty, otherwise we distributed incompatible components to a single device
check_incompatible(ComponentDistributionA,ComponentDistributionB) :-
  intersection(ComponentDistributionA,ComponentDistributionB, IntersectionList),
  % the resulting list should be of lenght 0, otherwise incompatible components are included
  length(IntersectionList,Len),
  Len =< 0.

% the amount of devices in ComponentDistributionB should be at least N * ComponentDistributionA,
% as each device in ComponentDistributionA depends on N devices in ComponentDistributionB
check_dependency_distinct(ComponentDistributionA,ComponentDistributionB, N) :-
  length(ComponentDistributionA,LenA),
  length(ComponentDistributionB,LenB),
  LenB >= N*LenA.

check_dependency(_,ComponentDistributionB, N) :-
  % dismiss ComponentDistributionA (_)
  length(ComponentDistributionB,LenB),
  LenB >= N.


get_available_devices(Devices) :-
  findall(X,property("device",1,X), Devices).

selection_disjunction([], _, OutputList):-
    OutputList = [].
selection_disjunction([[Key,Value, Boolean] | RestList], InputList, OutputList):-
    filter_list_by_property(property(Key,Value), Boolean, InputList, OutputListFiltered1),
    selection_disjunction(RestList, InputList, OutputListFiltered2),
    merge_list(OutputListFiltered1,OutputListFiltered2,OutputList).


filter_list_by_property(property(Key,Value), 1, InputList, OutputList):-
    include((property(Key,Value)), InputList, OutputList).
filter_list_by_property(property(Key,Value), 0, InputList, OutputList):-
    exclude((property(Key,Value)), InputList, OutputList).


apply_conjunction([], OutputList) :-
    OutputList = [].
apply_conjunction([List], OutputList) :-
    OutputList = List.
apply_conjunction([List|RestList], OutputList) :-
    apply_conjunction(RestList, OutputList1),
    intersection(List, OutputList1, OutputList).

include_lte(property(Key, Value), N, InputList, OutputList) :-
  findall(X,property(Key, Value,X),ListDevicesThatMatchProperty),
  intersection(InputList, ListDevicesThatMatchProperty, InputListIntersection),
  list_combinations_of_length_lte(InputListIntersection,N,InputListIntersectionComb),
  % remove all devices that match the property but keep devices on the generated list of possible combinations
  subtract(InputList,ListDevicesThatMatchProperty,InputListFiltered),
  append(InputListFiltered,InputListIntersectionComb,OutputList).


check_gte(property(Key, Value), N, InputList) :-
  findall(X,property(Key, Value,X),ListDevicesThatMatchProperty),
  intersection(InputList, ListDevicesThatMatchProperty, InputListIntersection),
  length(InputListIntersection,Len),
  Len >= N.

include_equal(property(Key, Value), N, InputList, OutputList) :-
  include_lte(property(Key, Value), N, InputList, OutputList),
  check_gte(property(Key, Value), N, OutputList).

check_include_all(property(Key, Value), AllPossibleList, InputList) :-
  include((property(Key, Value)),AllPossibleList,ListDevicesThatMatchProperty),
  intersection(InputList, ListDevicesThatMatchProperty, InputListIntersection),
  length(InputListIntersection, List_length),
  length(ListDevicesThatMatchProperty, List_length2),
  List_length == List_length2.

