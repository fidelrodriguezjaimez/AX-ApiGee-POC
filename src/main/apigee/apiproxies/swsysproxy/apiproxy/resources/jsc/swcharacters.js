const iataCode = context
  .getVariable("proxy.pathsuffix")
  .toUpperCase();

const allAirports = JSON.parse(context.getVariable("response.content"));
const matching = allAirports.count == iataCode;
console.log(matching);

if (matching) {
  context.setVariable("response.content", JSON.stringify(matching));
} else {
  setNotFoundError();
}

/**
 * Set the 404 Not Found error as a response
 */
function setNotFoundError() {
  const errorStatus = 404;
  const errorReason = "Not Found";
  const errrorContent = {
    errror: {
      errors: [
        {
          message: errorReason,
        },
      ],
      code: errorStatus,
      message: errorReason,
    },
  };

  context.setVariable("response.status.code", errorStatus);
  context.setVariable("response.reason.phrase", errorReason);
  context.setVariable("response.header.Content-Type", "application/json");
  context.setVariable("response.content", JSON.stringify(errrorContent));
}