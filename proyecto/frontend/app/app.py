from flask import Flask, render_template, send_from_directory, url_for, request, redirect, flash
from flask_login import LoginManager, login_manager, current_user, login_user, login_required, logout_user
import requests
import os

# Usuarios
from models import users, User

# Login
from forms import LoginForm, SignUpForm

app = Flask(__name__, static_url_path='')
login_manager = LoginManager()
login_manager.init_app(app) # Para mantener la sesión

# Configurar el secret_key. OJO, no debe ir en un servidor git público.
# Python ofrece varias formas de almacenar esto de forma segura, que
# no cubriremos aquí.
app.config['SECRET_KEY'] = 'qH1vprMjavek52cv7Lmfe1FoCexrrV8egFnB21jHhkuOHm8hJUe1hwn7pKEZQ1fioUzDb3sWcNK1pJVVIhyrgvFiIrceXpKJBFIn_i9-LTLBCc4cqaI3gjJJHU6kxuT8bnC7Ng'

@app.route('/static/<path:path>')
def serve_static(path):
    return send_from_directory('static', path)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    
    error = None
    form = LoginForm(None if request.method != 'POST' else request.form)

    if not(request.method == "POST"):
        error = 'Mala mia, algo ha fallado'
        return render_template('login.html', form=form,  error=error)
    
    if not sendLogin(form.email.data, form.password.data):
        error = 'Invalid Credentials. Please try again.'
        return render_template('login.html', form=form,  error=error)

    
    userInfo = getUserInfo(form.email.data)
    user = User(1, 
                userInfo['name'], 
                userInfo['email'], 
                userInfo['password'].encode('utf-8'))
    
    login_user(user)
    users.append(user)
    return redirect(url_for('profile'))


###@app.route('/signup', methods=['GET', 'POST'])
###def signup():

    ###if request.method == 'GET':
       ### return render_template('signup.html')
    
    ###form = SignUpForm(request.form)
    ##error = None
   ### name = form.name.data
###    email = form.email.data
    ###passw = form.password.data
    
    # Tratar error
###    if not sendSignUp(email, name, passw):
###        error = "Error al registrarse"
###        return render_template('signup.html', form=form, error=error)

###    print ("Registrado " + str(name) + " " + str(email))
###    return redirect(url_for('login'))

@app.route('/signup', methods=['GET', 'POST'])
def signup():
    if request.method == 'GET':
        return render_template('signup.html')
    
    form = SignUpForm(request.form)

    # Verificar formulario
    #if not form.validate():
    #    error = "Error al registrarse no form"
    #    app.logger.info(error)
    #    return render_template('signup.html', form=form, error=error)

    # Enviar solicitud al backend
    name = form.name.data
    email = form.email.data
    passw = form.password.data

    if not sendSignUp(email, name, passw):
        error = "Error al registrarse no signup"
        app.logger.info(error)
        flash(error)
        return render_template('signup.html', form=form, error=error)

    # Mostrar mensaje de éxito
    flash("¡Te has registrado con éxito!")
    return redirect(url_for('login'))


def flash_errors(form):
    """Flashes form errors"""
    for field, errors in form.errors.items():
        for error in errors:
            flash(u"Error in the %s field - %s" % (
                getattr(form, field).label.text,
                error
            ), 'error')

@app.route('/profile')
@login_required
def profile():
    return render_template('profile.html')

@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect(url_for('index'))

@login_manager.user_loader
def load_user(user_id):
    for user in users:
        if user.id == int(user_id):
            return user
    return None

def sendLogin(email, password):
    login = {'email': email, 'password': password}
    # Por defecto se crea una red con un servicio DNS, donde el nombre
    # de cada servidor es el nombre de cada servicio (diapositiva 38 - docker)
    respuesta = requests.post('http://backend-rest:8080/Service/checkLogin', json=login)
    flash(respuesta.status_code)
    if respuesta.status_code == 200:
        return True
    return False

###def sendSignUp(email, name, password):
###    registro = {'email':email, 'name':name, 'password':password}
    # Debug
###    app.logger.info("Sending register : " + str(registro))
###    respuesta = requests.post('http://backend-rest:8080/Service/registerUsr', json=registro)
###    app.logger.info("Response : " + str(respuesta.status_code))
###    flash(respuesta.status_code)
###    if respuesta.status_code == 200:
###  respuesta = ""
###     return True
###    return False
###
def sendSignUp(email, name, password):
   
    # Enviar solicitud al backend
    registro = {'email': email, 'name': name, 'password': password}
    try:
        respuesta = requests.post('http://backend-rest:8080/Service/registerUsr', json=registro)
    except requests.exceptions.RequestException as e:
        # Manejo de errores
        app.logger.error("Error sending registration request: " + str(e))
        return False

    # Manejo de respuestas del backend
    if respuesta.status_code == 201:
        return True
    else:
        # Otro error
        flash("Error al registrarse")
    return False


def getUserInfo(email):
    respuesta = requests.get('http://backend-rest:8080/Service/u/'+email)
    if respuesta.status_code == 200:
        return respuesta.json()
    return None

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
