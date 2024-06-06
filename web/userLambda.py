import boto3
def lambda_handler(event, context):
    type = event['type'];
    email =event['email'];
    user_name =event['user_name']
    password =event['password']

    client = boto3.resource('dynamodb')
   

    table = client.Table("login")
   
    if type=="get":
        password_value=table.get_item(Key={'email': email})['Item']['password']
        user_name_value=table.get_item(Key={'email': email})['Item']['user_name']
        resp = {
         'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'password': password_value,
                'user_name': user_name_value
            }
        }
    elif type=="options":
        db_value=table.get_item(Key={'email': email})['Item']['password']
        user_name_value=table.get_item(Key={'email': email})['Item']['user_name']
        resp = {
         'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'password': db_value,
                'user_name': user_name_value
            }
        }
    elif type=="update":
        table.put_item(Item= {'email': email,'user_name':  user_name, 'password': password}, ConditionExpression = "attribute_not_exists(email)")

        resp = {
            'statusCode': 200,
            'headers': {
                 'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'user_name': user_name
            }
        }
    else:
        resp = {
            'statusCode': 404,
            'headers': {
                 'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'error': 'type not found (e.g. post/get)'
            }
        }
    return resp
    


