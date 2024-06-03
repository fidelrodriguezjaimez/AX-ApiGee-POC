
/**
 * require wrapper without caching
 *
 * @param {*} module to be loaded
 * @return {*} freshly loaded module
 */

function requireUncached(module) {
    delete require.cache[require.resolve(module)];
    return require(module);
  }
  
  module.exports = requireUncached;