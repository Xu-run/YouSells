export class UnauthorizedError extends Error {
  constructor(message = "login required") {
    super(message);
    this.name = "UnauthorizedError";
  }
}

export function isUnauthorizedError(error: unknown): error is UnauthorizedError {
  return error instanceof Error && error.name === "UnauthorizedError";
}
