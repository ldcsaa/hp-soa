import os
import sys
import yaml

PRG_PATH = os.path.dirname(__file__)
DEF_CONF_FILE = PRG_PATH + os.path.sep + '..' + os.path.sep + 'deploy' + os.path.sep + 'bootstrap.yml'
DEF_ENV_NAME = 'local'


def parse_conf():
    conf_file = DEF_CONF_FILE
    env_name = DEF_ENV_NAME

    if len(sys.argv) > 1:
        env_name = sys.argv[1]
    if len(sys.argv) > 2:
        conf_file = sys.argv[2]

    conf = yaml.safe_load(open(conf_file, encoding='utf-8'))
    hp_soa_web = get_conf(None, 'hp.soa.web', False, conf)
    app = get_conf('hp.soa.web', 'app', True, hp_soa_web)

    if app:
        hp_soa_web_app_name_val = get_conf('hp.soa.web.app', 'name', False, app)
    else:
        hp_soa_web_app_name_val = get_conf('hp.soa.web', 'app.name', False, hp_soa_web)

    server_port_val = get_conf(None, 'server.port', True, conf)

    if not server_port_val:
        server = get_conf(None, 'server', False, conf)
        server_port_val = get_conf('server', 'port', False, server)
        
    startup_max_wait_seconds_val = get_conf(None, 'startup.max-wait-seconds', True, conf)

    if not startup_max_wait_seconds_val:
        startup = get_conf(None, 'startup', True, conf)
        if startup:
            startup_max_wait_seconds_val = get_conf('startup', 'max-wait-seconds', True, startup)

    jvm_options_env_val = get_conf(None, 'jvm.options' + '.' + env_name, True, conf)

    if not jvm_options_env_val:
        jvm_options = get_conf(None, 'jvm.options', True, conf)
        if jvm_options:
            if isinstance(jvm_options, str):
                jvm_options_env_val = jvm_options
            else:
                jvm_options_env_val = get_conf('jvm.options', env_name, True, jvm_options)

    print(('hp.soa.web.app.name={}#server.port={}#startup.max-wait-seconds={}#jvm.options' + '.' + env_name + '={}').format(hp_soa_web_app_name_val, server_port_val, (startup_max_wait_seconds_val if startup_max_wait_seconds_val else ''), (jvm_options_env_val if jvm_options_env_val else '')))


def get_conf(prefix, name, optional, conf):
    val = conf.get(name)
    if not optional and val is None:
        full_name = prefix + '.' + name if prefix else name
        raise RuntimeError('conf not found: \'{}\''.format(full_name))

    return val


if __name__ == '__main__':
    parse_conf()
