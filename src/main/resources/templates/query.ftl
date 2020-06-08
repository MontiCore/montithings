:- include('facts').
:- include('helpers').

<#list ast.distributions as distribution>
get_distribution(${distribution.name}) :-
    get_available_devices(AllAvailableDevices),

    % apply device properties that have to be matched
    include(property(state,online),AllAvailableDevices,AllAvailableDevicesFiltered1),
    <#assign count=1>
    <#list distribution.selectionConjunctionProperties as selection>
    include(property(${selection.key},${selection.value}),AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}),
        <#assign count++>
    </#list>

    % apply distribution constraints
    % first constrains less than equal: =<
    <#list distribution.lteConstraints as constraint>
    include_lte(property(${constraint.key}, ${constraint.value}), ${constraint.number}, AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}),
        <#assign count++>
    </#list>

    % then constrains equal: ==
    <#list distribution.equalConstraints as constraint>
    include_equal(property(${constraint.key}, ${constraint.value}), ${constraint.number}, AllAvailableDevicesFiltered${count},AllAvailableDevicesFiltered${count+1}),
        <#assign count++>
    </#list>

    % then constrains greater than equal: >=
    <#list distribution.gteConstraints as constraint>
    check_gte(property(${constraint.key}, ${constraint.value}), ${constraint.number}, AllAvailableDevicesFiltered${count}),
    </#list>

    % then constrains that check all equal
    <#list distribution.checkAllConstraints as constraint>
    check_include_all(property(${constraint.key}, ${constraint.value}), AllAvailableDevicesFiltered, AllAvailableDevicesFiltered${count}),
    </#list>

    % bind result to target variable
    AllAvailableDevicesFiltered${count} = ${distribution.name}.

</#list>

distribution(<#list ast.distributions as distribution>${distribution.name}<#sep>,</#sep></#list>) :-
    % retrieve possible lists of devices
    <#list ast.distributions as distribution>
    get_distribution(${distribution.name}),
    </#list>

    % apply incompatible checks
    <#list ast.incompatibilities as incompatibilitiesList>
        <#list incompatibilitiesList as key, value>
    check_incompatible(${key}, ${value}),
        </#list>
    </#list>

    % apply dependency checks
    <#list ast.dependencies as dependency>
        <#if dependency.type == "distinct">
    check_dependency_distinct(${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}),
        <#else>
    check_dependency(${dependency.dependent},${dependency.dependency},${dependency.amount_at_least}),
        </#if>
    </#list>

    % finishing query with a .
    1 == 1.