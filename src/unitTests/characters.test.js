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
  describe("Code Match", function () {
    it("should return an airport object with matching IATA code", function () {
      const mocks = mockFactory.getMock();

      mocks.contextGetVariableMethod
        .withArgs("response.content")
        .returns(JSON.stringify(charactersInfo));

      mocks.contextGetVariableMethod
        .withArgs("proxy.pathsuffix")
        .returns("82");

      let errorThrown = false;
      try {
        requireUncached(jsFile);
      } catch (e) {
        console.error(e);
        errorThrown = true;
      }
      assert(errorThrown === false, "ran without error");
      const spyResponse = mocks.contextSetVariableMethod.getCall(0).args[1];   
      const response = JSON.parse(spyResponse);
      assert.equal(response, true, "Response count correctly");
    });
  });
});