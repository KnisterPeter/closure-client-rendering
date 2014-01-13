## Clientside Extension for Closure-Templates

This repo contains a small javascript to extend the closure-template engine with includes and layouts on the client side.
The data attributes 'data-soy-include', 'data-soy-layout' and 'data-soy-insert' are used as marker where to process dom.

The URLs in 'data-soy-include' and 'data-soy-layout' attributes are specified as follows:
* The path defines the server-side template resource which must be returned as compiled javascript function from the server
* The hash defines the namespace and template-name to call


In current state a moden browser (Chrome, FF, IE9+) are required as well as jQuery 2. The included Java server is just for development purpose and is not required.

## Example

    {namespace test}

    /**
     *
     */
    {template .test}
      <ul>
        <li>a</li>
        <li>b</li>
        <li>c</li>
        <li>d</li>
        <li>e</li>
      </ul>
    {/template}

    /**
     *
     */
    {template .test2}
      <div data-soy-layout="/soy/test.html#test.layout">
        <span data-soy-include="/soy/test.html#test.test"></span>
      </div>
    {/template}

    /**
     *
     */
    {template .layout}
      <div style="font-weight:bold;" data-soy-insert>
      </div>
    {/template}

