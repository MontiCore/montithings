% (c) https://github.com/MontiCore/monticore
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

check_dependency(_,[],_,_,[],_,_).
check_dependency(MatchPredicate,[A|As],Bs,N,Dependencies,NameA,NameB) :-
  find_prop_match(MatchPredicate,A,Bs,BsFiltered),
  length(BsFiltered,LenBF),
  LenBF >= N,
  length(BsFilteredSub, N),
  sublist(BsFilteredSub, BsFiltered),
  % For performance reasons, we'll block backtracking here.
  % We will not find all possible dependency assignments, however
  % we do not need to, since this search is not distinct.
  !, 
  findall(dependsOn(bound(A,NameA),bound(B,NameB)), member(B,BsFilteredSub), OwnDeps),
  check_dependency(MatchPredicate,As,Bs,N,RemDeps,NameA,NameB),
  append(RemDeps,OwnDeps,Dependencies).


check_dependency_distinct(_,[],_,_,[],_,_).
check_dependency_distinct(MatchPredicate,[A|As],Bs,N,Dependencies,NameA,NameB) :-
  (
    find_prop_match(MatchPredicate,A,Bs,BsFiltered),
    length(BsFiltered,LenBF),
    LenBF >= N,
    !
  ),
  % Find all sublists with N elements
  length(BsFilteredSub, N),
  sublist(BsFilteredSub, BsFiltered),

  findall(dependsOn(bound(A,NameA),bound(B,NameB)), member(B,BsFilteredSub), OwnDeps),
  subtract(Bs,BsFilteredSub,BsRemoved),
  check_dependency_distinct(MatchPredicate,As,BsRemoved,N,RemDeps,NameA,NameB),
  append(RemDeps,OwnDeps,Dependencies).

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

check_lte(property(Key, Value), N, InputList) :-
  findall(X,property(Key, Value,X),ListDevicesThatMatchProperty),
  intersection(InputList, ListDevicesThatMatchProperty, InputListIntersection),
  length(InputListIntersection,Len),
  Len =< N.

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




sublist_max(Xs,Xs).
sublist_max(Xs,Ys) :- sublist_max_helper(Ys,Xs).
sublist_max_helper([],[]).
sublist_max_helper([H|T],[H|L]) :- sublist_max_helper(T,L).
sublist_max_helper([_|T],L) :- sublist_max_helper(T,L).

sublist([], _).
sublist([X|Xs], [X|Ys]) :- sublist(Xs, Ys).
sublist(Xs, [_|Ys]) :- sublist(Xs, Ys).

% match predicates for location matching
match_same_room(A,B) :-
  property("location_building", Building, A),
  property("location_building", Building, B),
  property("location_floor", Floor, A),
  property("location_floor", Floor, B),
  property("location_room", Room, A),
  property("location_room", Room, B).

match_same_floor(A,B) :-
  property("location_building", Building, A),
  property("location_building", Building, B),
  property("location_floor", Floor, A),
  property("location_floor", Floor, B).

match_same_building(A,B) :-
  property("location_building", Building, A),
  property("location_building", Building, B).

match_any(_,_).

find_prop_match(_,_,[],[]).
find_prop_match(MatchPredicate,A,[B|Bs],Ms) :-
  (
    (call(MatchPredicate, A, B),!,Ms=[B|Msr]);
    Ms=Msr
  ),
  find_prop_match(MatchPredicate,A,Bs,Msr).
