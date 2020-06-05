:- include('facts').


<#list ast.properties as config, props>
    <#if config.equals("incompatibilities")>

    </#if>
    <#list props as key, value>
        property(${key}, ${value}, ${device}).
    </#list>
</#list>


get_distribution(Droom_temp_sensor_latest) :-
    get_available_devices(Devices),
    % apply device properties that have to be matched
    include(property(state,online),Devices,Devices2),
    include(property(has_hardware,sensor_temperature),Devices,Devices3),

    % apply distribution constraints
    % first constrains less than equal: =<

    % then constrains greater than equal: >=

get_distribution(Droom_temp_controller_latest) :-
    get_available_devices(Devices),
    % apply device properties that have to be matched

    % apply distribution constraints
    % first constrains less than equal: =<

    % then constrains greater than equal: >=

distribution(Droom_temp_controller_latest, Droom_temp_sensor_latest) :-
    % retrieve possible lists of devices
    get_distribution(Droom_temp_controller_latest),
    get_distribution(Droom_temp_sensor_latest),

    % apply incompatible checks
    <#list ast.properties as config, props>
        <#if config.equals("incompatibilities")>
            <#list props as key, value>
                check_incompatible(Droom_temp_controller_latest,Droom_temp_sensor_latest),
                check_incompatible(${value[0]}, ${value[1]}).
            </#list>
        </#if>

    </#list>
    % apply dependency checks