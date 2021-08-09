<#macro printPortSpyTemplate >
/**
 * This (abstract) class records all messages going through port.
 * The recorded messages can then be checked by the test case against the expected values.
 *
 * \tparam ComponentType class of the component this spy is attached to
 * \tparam PortType typename of the messages going through the port
 */
template <typename ComponentType, typename PortType> class PortSpy : public EventObserver
{
  protected:
    ComponentType *component;
    std::vector<tl::optional<Message<PortType>>> recordedMessages;

  public:
    explicit PortSpy (ComponentType *component) : component (component) {}

    const std::vector<tl::optional<Message<PortType>>> &
    getRecordedMessages () const
    {
      return recordedMessages;
    }
};
</#macro>
