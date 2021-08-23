package io.sentry;

import io.sentry.hints.SessionEndHint;
import io.sentry.hints.SessionStartHint;
import io.sentry.util.Objects;
import org.jetbrains.annotations.NotNull;

final class ClientSessionTracker implements SessionTracker {
  private final @NotNull Stack stack;
  private final @NotNull SentryOptions options;

  ClientSessionTracker(final @NotNull SentryOptions options, final @NotNull Stack stack) {
    this.options = Objects.requireNonNull(options, "SentryOptions is required");
    this.stack = Objects.requireNonNull(stack, "Stack is required");
  }

  @Override
  public void startSession() {
    final Stack.StackItem item = this.stack.peek();
    final Scope.SessionPair pair = item.getScope().startSession();
    if (pair != null) {
      // TODO: add helper overload `captureSessions` to pass a list of sessions and submit a
      // single envelope
      // Or create the envelope here with both items and call `captureEnvelope`
      if (pair.getPrevious() != null) {
        item.getClient().captureSession(pair.getPrevious(), new SessionEndHint());
      }

      item.getClient().captureSession(pair.getCurrent(), new SessionStartHint());
    } else {
      options.getLogger().log(SentryLevel.WARNING, "Session could not be started.");
    }
  }

  @Override
  public void endSession() {
    final Stack.StackItem item = this.stack.peek();
    final Session previousSession = item.getScope().endSession();
    if (previousSession != null) {
      item.getClient().captureSession(previousSession, new SessionEndHint());
    }
  }
}