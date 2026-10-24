package dslabs.atmostonce;

import dslabs.framework.Address;
import dslabs.framework.Application;
import dslabs.framework.Command;
import dslabs.framework.Result;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public final class AMOApplication<T extends Application> implements Application {
  @Getter @NonNull private final T application;

  // Your code here...
  private Address address;
  private AMOResult amoResult;
  private final Map<Address, AMOResult> map = new HashMap<>();

  @Override
  public AMOResult execute(Command command) {
    if (!(command instanceof AMOCommand)) {
      throw new IllegalArgumentException();
    }

    AMOCommand amoCommand = (AMOCommand) command;

    // Your code here...
    if (alreadyExecuted(amoCommand)) {
      AMOResult result = map.get(amoCommand.address());
      return result;
    }

    Result result = application.execute(amoCommand.command());
    AMOResult amoResultLocal = new AMOResult(result, amoCommand.sequenceNum());
    map.put(amoCommand.address(), amoResultLocal);
    return amoResultLocal;
  }

  public Result executeReadOnly(Command command) {
    if (!command.readOnly()) {
      throw new IllegalArgumentException();
    }

    if (command instanceof AMOCommand) {
      return execute(command);
    }

    return application.execute(command);
  }

  public boolean alreadyExecuted(AMOCommand amoCommand) {
    // Your code here...
    AMOResult amoResult = map.get(amoCommand.address());
    if (amoResult == null) {
      return false;
    }

    if (amoResult.sequenceNum() >= amoCommand.sequenceNum()) {
      return true;
    }
    return false;
  }
}
