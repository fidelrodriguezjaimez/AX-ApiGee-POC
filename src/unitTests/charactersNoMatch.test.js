const assert = require("assert");
const mockFactory = require("./common/mockFactory");

const charactersInfo = require("./common/testCharacters");
const requireUncached = require("./common/requireUncached");
const path = require('path');

// Ruta al directorio actual
const directorioActual = __dirname;

// Ruta al directorio padre
const directorioPadre = path.join(directorioActual, '..');
const jsFile = directorioPadre + "/main/apigee/apiproxies/swsysproxy/apiproxy/resources/jsc/swcharacters.js";

describe("Airport By IATA Code", function () {
  describe("No Code Match", function () {
    it("should return a 404 error code if no match is found.", function () {
      const mocks = mockFactory.getMock();
      mocks.contextGetVariableMethod
        .withArgs("response.content")
        .returns(JSON.stringify(charactersInfo));
      mocks.contextGetVariableMethod
        .withArgs("proxy.pathsuffix")
        .returns("80");

      let errorThrown = false;
      try {
        requireUncached(jsFile);
      } catch (e) {
        console.error(e);
        errorThrown = true;
      }
      assert(errorThrown === false, "ran without error");
      assert(
        mocks.contextSetVariableMethod.calledWith("response.status.code", 404),
        "response.status.code set to 404"
      );
    });
  });
});